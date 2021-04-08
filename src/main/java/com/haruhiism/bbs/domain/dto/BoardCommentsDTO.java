package com.haruhiism.bbs.domain.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class BoardCommentsDTO {

    @NonNull
    private List<BoardCommentDTO> boardComments;

    @NonNull
    private int currentPage;
    @NonNull
    private int totalPages;
}
