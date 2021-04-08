package com.haruhiism.bbs.domain.entity;

import lombok.*;

import javax.persistence.*;

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


    public void changeTitle(String title){
        this.title = title;
    }

    public void changeContent(String content){
        this.content = content;
    }
}
