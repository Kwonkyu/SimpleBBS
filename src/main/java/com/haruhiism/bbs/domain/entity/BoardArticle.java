package com.haruhiism.bbs.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "articleID")
@Entity
@RequiredArgsConstructor
public class BoardArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleID;

    @NonNull
    private String writer;
    @NonNull
    private String password;
    @NonNull
    private String title;
    @NonNull
    private String content;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "articleid")
    private List<BoardComment> comments;

    public BoardArticle(){}
}
