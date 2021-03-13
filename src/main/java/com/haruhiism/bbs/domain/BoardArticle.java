package com.haruhiism.bbs.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "bid")
@Entity
@RequiredArgsConstructor
public class BoardArticle {
    // TODO: apply validation
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bid;

    @NonNull
    private String writer;
    @NonNull
    private String password;
    @NonNull
    private String title;
    @NonNull
    private String content;

    public BoardArticle(){}
}
