package com.haruhiism.bbs.service;

import com.haruhiism.bbs.domain.dto.*;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import com.haruhiism.bbs.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PageUtility {

    private final CommentRepository commentRepository;

    public BoardArticlesDTO convertBoardArticles(Page<BoardArticle> result){
        List<BoardArticleDTO> articles = new ArrayList<>();
        List<Integer> commentSizes = new ArrayList<>();

        result.get().forEachOrdered(boardArticle -> {
            articles.add(new BoardArticleDTO(boardArticle));
            commentSizes.add(commentRepository.countAllByBoardArticleAndDeletedFalse(boardArticle));
        });

        return BoardArticlesDTO.builder()
                .boardArticles(articles)
                .boardArticleCommentSize(commentSizes)
                .currentPage(result.getNumber())
                .totalPages(result.getTotalPages()).build();
    }

    public BoardArticlesDTO generateEmptyBoardArticles(){
        return BoardArticlesDTO.builder()
                .boardArticles(new ArrayList<>())
                .boardArticleCommentSize(new ArrayList<>())
                .currentPage(0)
                .totalPages(0).build();
    }


    public BoardCommentsDTO convertBoardComments(Page<BoardComment> result){
        return BoardCommentsDTO.builder()
                .boardComments(result.map(BoardCommentDTO::new).toList())
                .totalPages(result.getTotalPages())
                .currentPage(result.getNumber()).build();
    }

    public BoardCommentsDTO generateEmptyBoardComments(){
        return BoardCommentsDTO.builder()
                .boardComments(new ArrayList<>())
                .currentPage(0)
                .totalPages(0).build();
    }


    public BoardAccountsDTO convertBoardAccounts(Page<BoardAccount> result){
        return BoardAccountsDTO.builder()
                .accounts(result.map(BoardAccountDTO::new).toList())
                .totalPage(result.getTotalPages())
                .currentPage(result.getNumber()).build();
    }

    public BoardAccountsDTO generateEmptyBoardAccounts(){
        return BoardAccountsDTO.builder()
                .accounts(new ArrayList<>())
                .currentPage(0)
                .totalPage(0).build();
    }
}
