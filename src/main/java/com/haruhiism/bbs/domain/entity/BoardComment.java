package com.haruhiism.bbs.domain.entity;

import com.haruhiism.bbs.domain.dto.BoardCommentDTO;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class BoardComment extends MACDate{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_COMMENT_ID")
    private long id;

    @NonNull
    @Column(name = "WRITER")
    private String writer;

    @NonNull
    @Column(name = "PASSWORD")
    private String password;

    @NonNull
    @Column(name = "CONTENT")
    private String content;

    @NonNull @ManyToOne
    @JoinColumn(name = "BOARD_ARTICLE_ID")
    private BoardArticle boardArticle;

    @ManyToOne
    @JoinColumn(name = "BOARD_ACCOUNT_ID")
    private BoardAccount boardAccount;

    @Column(name = "DELETED")
    private boolean deleted = false;


    public void toggleDeletedStatus(){
        deleted = !deleted;
    }

    public void delete(){
        deleted = true;
    }

    public void restore(){
        deleted = false;
    }

    public void registerWriter(BoardAccount boardAccount){
        this.boardAccount = boardAccount;
        this.writer = boardAccount.getUsername();
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public boolean isWrittenByAccount(){
        return boardAccount != null;
    }


    public BoardComment(BoardArticle article, BoardCommentDTO.Submit dto) {
        this.boardArticle = article;
        this.writer = dto.getWriter();
        this.content = dto.getContent();
        this.password = dto.getPassword();
    }

    public BoardComment(BoardArticle article, BoardAccount boardAccount, BoardCommentDTO.Submit dto) {
        this.boardArticle = article;
        this.boardAccount = boardAccount;
        this.writer = boardAccount.getAlias();
        this.content = dto.getContent();
        this.password = dto.getPassword();
    }


}
