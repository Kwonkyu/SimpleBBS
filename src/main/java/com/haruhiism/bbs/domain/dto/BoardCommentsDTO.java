package com.haruhiism.bbs.domain.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
public class BoardCommentsDTO {

    private List<BoardCommentDTO> boardComments;

    private int currentPage;
    private int totalPages;

}
