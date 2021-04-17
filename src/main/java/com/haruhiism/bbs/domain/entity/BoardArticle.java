package com.haruhiism.bbs.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class BoardArticle {

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
