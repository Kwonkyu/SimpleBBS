package com.haruhiism.bbs.domain.dto;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BoardArticlesDTO {

    private List<BoardArticleDTO> boardArticles;
    private List<Integer> boardArticleCommentSize;

    private int currentPage;
    private int totalPages;

    @Override
    public String toString() {
        return String.format("[%d] articles with page %d of %d.\n", boardArticles.size(), currentPage, totalPages);
    }
}
