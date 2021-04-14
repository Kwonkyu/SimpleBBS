package com.haruhiism.bbs.domain.dto;


import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class BoardArticlesDTO {

    @NonNull
    private List<BoardArticleDTO> boardArticles;
    @NonNull
    private List<Integer> boardArticleCommentSize;

    @NonNull
    private int currentPage;
    @NonNull
    private int totalPages;

    @Override
    public String toString() {
        return String.format("[%d] articles with page %d of %d.\n", boardArticles.size(), currentPage, totalPages);
    }
}
