package com.haruhiism.bbs.domain.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "commentID")
@Entity
@RequiredArgsConstructor
public class BoardComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentID;

    @NonNull
    private String writer;
    @NonNull
    private String password;
    @NonNull
    private String content;
    @NonNull
    private Long articleID;

    public BoardComment(){}
}
