package com.haruhiism.bbs.domain.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BoardCommentsDTO {

    private List<BoardCommentDTO> boardComments;

    private int currentPage;
    private int totalPages;
}
