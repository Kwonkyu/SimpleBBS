package com.haruhiism.bbs.domain.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class BoardCommentDTO {

    private Long commentId;
    private Long accountID;
    @NonNull
    private Long articleID;
    @NonNull
    private String writer;
    @NonNull
    private String password;
    @NonNull
    private String content;
}
