package com.haruhiism.bbs.domain.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class BoardComment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_COMMENT_ID")
    private Long id;
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

    public void registerCommentWriter(BoardAccount boardAccount){
        this.boardAccount = boardAccount;
        this.writer = boardAccount.getUsername();
    }

    public boolean isWrittenByLoggedInAccount(){
        return boardAccount != null;
    }
}
