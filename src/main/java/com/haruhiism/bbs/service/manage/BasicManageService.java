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

            case ACCOUNT:
                Optional<BoardAccount> account = accountRepository.findByUserId(keyword);
                if(account.isPresent()){
                    return pageUtility.convertBoardArticles(
                            articleRepository.findAllByBoardAccount(account.get(), page));
                } else {
                    return pageUtility.generateEmptyBoardArticles();
                }

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

        switch(commentSearchMode){
            case WRITER:
                return pageUtility.convertBoardComments(
                        commentRepository.findAllByWriterContainingAndCreatedDateTimeBetween(keyword, from, to, PageRequest.of(pageNum, pageSize)));

            case CONTENT:
                return pageUtility.convertBoardComments(
                        commentRepository.findAllByContentContainingAndCreatedDateTimeBetween(keyword, from, to, PageRequest.of(pageNum, pageSize)));

            case ARTICLE:
                Optional<BoardArticle> article = articleRepository.findById(Long.parseLong(keyword));
                if(article.isPresent()){
                    return pageUtility.convertBoardComments(
                            commentRepository.findAllByBoardArticleAndCreatedDateTimeBetween(article.get(), from, to, PageRequest.of(pageNum, pageSize)));
                } else {
                    return pageUtility.generateEmptyBoardComments();
                }

            case ACCOUNT:
                Optional<BoardAccount> account = accountRepository.findByUserId(keyword);
                if(account.isPresent()) {
                    return pageUtility.convertBoardComments(
                            commentRepository.findAllByBoardAccountAndCreatedDateTimeBetween(account.get(), from, to, PageRequest.of(pageNum, pageSize)));
                } else {
                    return pageUtility.generateEmptyBoardComments();
                }

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
