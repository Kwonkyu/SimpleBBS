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
public class BoardArticle {
    // TODO: apply validation
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bid;

    private String writer;
    private String title;
    private String content;
}
