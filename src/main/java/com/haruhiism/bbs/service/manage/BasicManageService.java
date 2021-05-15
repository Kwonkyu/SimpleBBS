package com.haruhiism.bbs.service.manage;

import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.domain.AccountSearchMode;
import com.haruhiism.bbs.domain.ArticleSearchMode;
import com.haruhiism.bbs.domain.CommentSearchMode;
import com.haruhiism.bbs.domain.dto.*;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardAccountLevel;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import com.haruhiism.bbs.exception.account.NoAccountFoundException;
import com.haruhiism.bbs.repository.AccountLevelRepository;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.repository.CommentRepository;
import com.haruhiism.bbs.service.DataEncoder.DataEncoder;
import com.haruhiism.bbs.service.PageUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
@Transactional
@RequiredArgsConstructor
public class BasicManageService implements AccountManagerService, ArticleManagerService, CommentManagerService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final AccountRepository accountRepository;
    private final AccountLevelRepository accountLevelRepository;

    private final PageUtility pageUtility;

    private final DataEncoder dataEncoder;

    // Article Services.

    @Override
    @Transactional(readOnly = true)
    public Long countAllArticles() {
        return articleRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAllDeletedArticles() {
        return articleRepository.countAllByDeletedTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAllNotDeletedArticles() {
        return articleRepository.count() - articleRepository.countAllByDeletedTrue();
    }


    @Override
    @Transactional(readOnly = true)
    public BoardArticlesDTO readArticles(int pageNum, int pageSize, LocalDateTime from, LocalDateTime to) {
        Page<BoardArticle> articles = articleRepository.findAllByCreatedDateTimeBetweenOrderByIdDesc(from, to, PageRequest.of(pageNum, pageSize));
        return pageUtility.convertBoardArticles(articles);
    }


    @Override
    public void deleteArticles(List<Long> articleIds) {
        for (Long articleId : articleIds) {
            articleRepository.findById(articleId).ifPresent(BoardArticle::delete);
        }
    }

    @Override
    public void restoreArticles(List<Long> articleIds) {
        for (Long articleId : articleIds) {
            articleRepository.findById(articleId).ifPresent(BoardArticle::restore);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public BoardArticlesDTO searchArticles(ArticleSearchMode articleSearchMode, String keyword, int pageNum, int pageSize, LocalDateTime from, LocalDateTime to) {
        Page<BoardArticle> result;
        PageRequest page = PageRequest.of(pageNum, pageSize);
        switch (articleSearchMode) {
            case TITLE:
                result = articleRepository.findAllByTitleContainingAndCreatedDateTimeBetweenOrderByIdDesc(keyword, from, to, page);
                break;

            case WRITER:
                result = articleRepository.findAllByWriterContainingAndCreatedDateTimeBetweenOrderByIdDesc(keyword, from, to, page);
                break;

            case CONTENT:
                result = articleRepository.findAllByContentContainingAndCreatedDateTimeBetweenOrderByIdDesc(keyword, from, to, page);
                break;

            case TITLE_CONTENT:
                result = articleRepository.findAllByTitleContainingAndCreatedDateTimeBetweenOrContentContainingAndCreatedDateTimeBetweenOrderByIdDesc(
                        keyword, from, to, keyword, from, to, page);
                break;

            default:
                throw new UnsupportedOperationException();
        }

        return pageUtility.convertBoardArticles(result);
    }



    // Comment Services.

    @Override
    @Transactional(readOnly = true)
    public Long countAllComments() {
        return commentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAllDeletedComments() {
        return commentRepository.countAllByDeletedTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAllNotDeletedComments() {
        return commentRepository.count() - commentRepository.countAllByDeletedTrue();
    }


    @Override
    @Transactional(readOnly = true)
    public BoardCommentsDTO readComments(int pageNum, int pageSize, LocalDateTime from, LocalDateTime to) {
        Page<BoardComment> comments = commentRepository.findAllByCreatedDateTimeBetween(from, to, PageRequest.of(pageNum, pageSize));
        return pageUtility.convertBoardComments(comments);
    }

    @Override
    public void deleteComments(List<Long> commentIds) {
        for (Long commentId : commentIds) {
            commentRepository.findById(commentId).ifPresent(BoardComment::delete);
        }
    }

    @Override
    public void restoreComments(List<Long> commentIds) {
        for (Long commentId : commentIds) {
            commentRepository.findById(commentId).ifPresent(BoardComment::restore);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public BoardCommentsDTO searchComments(CommentSearchMode commentSearchMode, String keyword, int pageNum, int pageSize, LocalDateTime from, LocalDateTime to) {

        // TODO: 사용자 이름이 변경되었을 때 로그인한 사용자가 작성한 데이터에는 변경 전 이름이 남아있지만 실제로 출력되는 것은 변경 후 이름.
        // TODO: SQL에서 이름을 검색할 때 변경 전 이름이 남아서 문제가 발생. 로그인한 사용자가 작성한 댓글이라면 해당 사용자의 username으로 검색할 것.
        // TODO: 혹은 임시방편으로 관리자 페이지에서는 변경된 이름이 아니라 원래 이름을 보여준다던가
        // TODO: 아니면 로직을 바꿔서 사용자명이 변경됐을 때 기존에 작성된 게시글은 작성자 이름을 그대로 유지하도록?

        switch(commentSearchMode){
            case WRITER:
                return pageUtility.convertBoardComments(
                        commentRepository.findAllByWriterContainingAndCreatedDateTimeBetween(keyword, from, to, PageRequest.of(pageNum, pageSize)));

            case CONTENT:
                return pageUtility.convertBoardComments(
                        commentRepository.findAllByContentContainingAndCreatedDateTimeBetween(keyword, from, to, PageRequest.of(pageNum, pageSize)));

            case ARTICLE:
                BoardArticle article = articleRepository.findById(Long.parseLong(keyword)).orElse(new BoardArticle());
                return pageUtility.convertBoardComments(
                        commentRepository.findAllByBoardArticleAndCreatedDateTimeBetween(article, from, to, PageRequest.of(pageNum, pageSize)));

            case ACCOUNT:
                BoardAccount account = accountRepository.findByUserId(keyword).orElse(new BoardAccount());
                return pageUtility.convertBoardComments(
                        commentRepository.findAllByBoardAccountAndCreatedDateTimeBetween(account, from, to, PageRequest.of(pageNum, pageSize)));

            default:
                throw new UnsupportedOperationException();
        }
    }



    // Account Services.

    @Override
    @Transactional(readOnly = true)
    public Long countAllAccounts() {
        return accountRepository.count();
    }


    @Override
    @Transactional(readOnly = true)
    public List<AccountLevel> getLevelOfAccount(BoardAccountDTO boardAccountDTO) {
        BoardAccount boardAccount = accountRepository.findByUserIdAndAvailableTrue(boardAccountDTO.getUserId())
                .orElseThrow(NoAccountFoundException::new);
        return accountLevelRepository.findAllByBoardAccount(boardAccount)
                .stream().map(BoardAccountLevel::getAccountLevel).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BoardAccountsDTO readAccounts(int pageNum, int pageSize, LocalDateTime from, LocalDateTime to) {
        return pageUtility.convertBoardAccounts(accountRepository.findAllByCreatedDateTimeBetween(from, to, PageRequest.of(pageNum, pageSize)));
    }

    @Override
    @Transactional(readOnly = true)
    public BoardAccountsDTO searchAccounts(AccountSearchMode mode, String keyword, int pageNum, int pageSize, LocalDateTime from, LocalDateTime to) {
        switch(mode){
            case EMAIL:
                return pageUtility.convertBoardAccounts(
                        accountRepository.findAllByEmailContainingAndCreatedDateTimeBetween(keyword, from, to, PageRequest.of(pageNum, pageSize)));

            case USERID:
                return pageUtility.convertBoardAccounts(
                        accountRepository.findAllByUserIdContainingAndCreatedDateTimeBetween(keyword, from, to, PageRequest.of(pageNum, pageSize)));

            case USERNAME:
                return pageUtility.convertBoardAccounts(
                        accountRepository.findAllByUsernameContainingAndCreatedDateTimeBetween(keyword, from, to, PageRequest.of(pageNum, pageSize)));

            default:
                throw new UnsupportedOperationException();

        }
    }

    @Override
    public void invalidateAccounts(List<Long> accountIds) {
        for (Long accountId : accountIds) {
            accountRepository.findById(accountId).ifPresent(BoardAccount::invalidate);
        }
    }

    @Override
    public void restoreAccounts(List<Long> accountIds) {
        for (Long accountId : accountIds) {
            accountRepository.findById(accountId).ifPresent(BoardAccount::restore);
        }
    }

    // TODO: 이렇게 일괄로 변경시키는 방식이 옳은 것인지?
    @Override
    public void changePassword(List<Long> accountIds, String newPassword) {
        for (Long accountId : accountIds) {
            accountRepository.findById(accountId).ifPresent(boardAccount -> boardAccount.changePassword(dataEncoder.encode(newPassword)));
        }
    }

    @Override
    public void changeUsername(List<Long> accountIds, String newUsername) {
        for (Long accountId : accountIds) {
            accountRepository.findById(accountId).ifPresent(boardAccount -> boardAccount.changeUsername(newUsername));
        }
    }
}
