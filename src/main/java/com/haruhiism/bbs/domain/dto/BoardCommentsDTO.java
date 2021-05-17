package com.haruhiism.bbs.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BoardCommentsDTO {

    private List<BoardCommentDTO> boardComments;

    private int currentPage;
    private int totalPages;

}
