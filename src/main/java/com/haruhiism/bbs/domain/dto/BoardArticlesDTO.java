package com.haruhiism.bbs.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class BoardArticlesDTO extends DTOContainer {

    private List<BoardArticleDTO> boardArticles;
    private List<Integer> boardArticleCommentSize;

    @Builder
    public BoardArticlesDTO(int currentPage, int totalPages, List<BoardArticleDTO> boardArticles, List<Integer> boardArticleCommentSize){
        super(currentPage, totalPages);
        this.boardArticles = boardArticles;
        this.boardArticleCommentSize = boardArticleCommentSize;
    }
}
