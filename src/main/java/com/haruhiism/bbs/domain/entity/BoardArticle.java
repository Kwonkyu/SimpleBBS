package com.haruhiism.bbs.domain.entity;

import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.haruhiism.bbs.domain.dto.BoardArticleDTO.*;

@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "BOARD_ARTICLE")
public class BoardArticle extends MACDate{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_ARTICLE_ID")
    private long id;

    @NonNull
    @Column(name = "WRITER")
    private String writer;

    @NonNull
    @Column(name = "PASSWORD")
    private String password;

    @NonNull
    @Column(name = "TITLE")
    private String title;

    @NonNull
    @Column(name = "CONTENT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "BOARD_ACCOUNT_ID")
    private BoardAccount boardAccount;

    @OneToMany(mappedBy = "boardArticle")
    private final List<BoardComment> comments = new ArrayList<>();


    @Column(name = "DELETED")
    private boolean deleted = false;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "HIT_ID")
    private final ReadHit hit = new ReadHit();


    public void increaseHit(){
        hit.increaseHit();
    }

    public void toggleDeletedStatus(){
        deleted = !deleted;
    }

    public void delete(){
        deleted = true;
    }

    public void restore(){
        deleted = false;
    }

    public void changePassword(String password){
        this.password = password;
    }

    public void changeTitle(String title){
        this.title = title;
    }

    public void changeContent(String content){
        this.content = content;
    }

    public void registerAccountInfo(BoardAccount boardAccount) {
        this.boardAccount = boardAccount;
        this.writer = boardAccount.getUsername();
    }


    public BoardArticle(Submit article) {
        this.writer = article.getWriter();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.password = article.getPassword();
    }

    public BoardArticle(Submit article, BoardAccount account) {
        this.writer = account.getAlias();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.boardAccount = account;
        this.password = article.getPassword();
    }
}
