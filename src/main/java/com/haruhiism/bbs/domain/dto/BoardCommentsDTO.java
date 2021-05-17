package com.haruhiism.bbs.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class BoardCommentsDTO extends DTOContainer {

    private List<BoardCommentDTO> boardComments;

    @Builder
    public BoardCommentsDTO(int currentPage, int totalPages, List<BoardCommentDTO> boardComments){
        super(currentPage, totalPages);
        this.boardComments = boardComments;
    }
}
