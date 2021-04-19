package com.haruhiism.bbs.domain.entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

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
    private Long id;
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
    // TODO: implement test codes for delete feature?
    @Column(name = "DELETED")
    private boolean deleted = false;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "HIT_ID")
    private ReadHit hit = new ReadHit();

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

    @Override
    public String toString() {
        return String.format("[#%d] '%s' written by '%s'.\nContents: %10s... [ DELETED = %s ]\n",
                id, title, writer, content, deleted);
    }
}
