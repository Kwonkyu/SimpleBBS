package com.haruhiism.bbs.service;

import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import com.haruhiism.bbs.exception.account.NoAccountFoundException;
import com.haruhiism.bbs.exception.article.NoArticleFoundException;
import com.haruhiism.bbs.exception.comment.NoCommentFoundException;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class RepositoryUtility {
    public static BoardAccount findAccountByUserId(AccountRepository accountRepository, String userId) {
        return accountRepository.findByUserIdAndAvailableTrue(userId).orElseThrow(NoAccountFoundException::new);
    }

    public static BoardArticle findArticleById(ArticleRepository articleRepository, long id) {
        return articleRepository.findByIdAndDeletedFalse(id).orElseThrow(NoArticleFoundException::new);
    }

    public static BoardComment findCommentById(CommentRepository commentRepository, long id) {
        return commentRepository.findById(id).orElseThrow(NoCommentFoundException::new);
    }

    public static Page<BoardComment> findCommentsByArticle(CommentRepository commentRepository, BoardArticle article, int pageNum, int pageSize) {
        return commentRepository.findAllByBoardArticleAndDeletedFalseOrderByIdAsc(article, PageRequest.of(pageNum, pageSize));
    }

    public static Page<BoardComment> findCommentsByAccount(CommentRepository commentRepository, BoardAccount account, int pageNum, int pageSize) {
        return commentRepository.findAllByBoardAccountAndDeletedFalse(account, PageRequest.of(pageNum, pageSize));
    }
}
