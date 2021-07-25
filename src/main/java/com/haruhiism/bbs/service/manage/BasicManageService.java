package com.haruhiism.bbs.service.manage;

import com.haruhiism.bbs.command.manage.AccountLevelManagementCommand;
import com.haruhiism.bbs.domain.ManagerLevel;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentDTO;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardAccountLevel;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import com.haruhiism.bbs.exception.account.NoAccountFoundException;
import com.haruhiism.bbs.exception.article.NoArticleFoundException;
import com.haruhiism.bbs.repository.AccountLevelRepository;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.haruhiism.bbs.command.manage.AccountLevelManagementCommand.LevelOperation.*;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicManageService implements AccountManagerService, ArticleManagerService, CommentManagerService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final AccountRepository accountRepository;
    private final AccountLevelRepository accountLevelRepository;
    private final PasswordEncoder passwordEncoder;

    // Article Services.

    @Override
    @Transactional(readOnly = true)
    public long countAllArticles() {
        return articleRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllDeletedArticles() {
        return articleRepository.countAllByDeletedTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllNotDeletedArticles() {
        return articleRepository.count() - articleRepository.countAllByDeletedTrue();
    }


    @Override
    @Transactional(readOnly = true)
    public BoardArticleDTO.PagedArticles readArticlesPage(int pageNum, int pageSize, LocalDateTime from, LocalDateTime to) {
        return new BoardArticleDTO.PagedArticles(
                articleRepository.findAllByCreatedDateTimeBetweenOrderByIdDesc(from, to, PageRequest.of(pageNum, pageSize)));
    }


    @Override
    public void deleteArticles(List<Long> articleIds) {
        for (long articleId : articleIds) {
            articleRepository.findById(articleId).ifPresent(BoardArticle::delete);
        }
    }

    @Override
    public void restoreArticles(List<Long> articleIds) {
        for (long articleId : articleIds) {
            articleRepository.findById(articleId).ifPresent(BoardArticle::restore);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public BoardArticleDTO.PagedArticles searchArticlesPage(BoardArticleDTO.ArticleSearchMode articleSearchMode, String keyword, int pageNum, int pageSize, LocalDateTime from, LocalDateTime to) {
        Page<BoardArticle> result = Page.empty();
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
                BoardAccount account = accountRepository.findByUserId(keyword).orElseThrow(NoAccountFoundException::new);
                result = articleRepository.findAllByBoardAccount(account, page);
        }

        return new BoardArticleDTO.PagedArticles(result);
    }



    // Comment Services.

    @Override
    @Transactional(readOnly = true)
    public long countAllComments() {
        return commentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllDeletedComments() {
        return commentRepository.countAllByDeletedTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllNotDeletedComments() {
        return commentRepository.count() - commentRepository.countAllByDeletedTrue();
    }


    @Override
    @Transactional(readOnly = true)
    public BoardCommentDTO.PagedComments readCommentsPage(int pageNum, int pageSize, LocalDateTime from, LocalDateTime to) {
        return new BoardCommentDTO.PagedComments(commentRepository.findAllByCreatedDateTimeBetweenOrderByIdDesc(from, to, PageRequest.of(pageNum, pageSize)));
    }

    @Override
    public void deleteComments(List<Long> commentIds) {
        for (long commentId : commentIds) {
            commentRepository.findById(commentId).ifPresent(BoardComment::delete);
        }
    }

    @Override
    public void restoreComments(List<Long> commentIds) {
        for (long commentId : commentIds) {
            commentRepository.findById(commentId).ifPresent(BoardComment::restore);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public BoardCommentDTO.PagedComments searchCommentsPage(BoardCommentDTO.CommentSearchMode commentSearchMode, String keyword, int pageNum, int pageSize, LocalDateTime from, LocalDateTime to) {
        Page<BoardComment> result = Page.empty();
        PageRequest page = PageRequest.of(pageNum, pageSize);

        // TODO: dynamic query
        switch(commentSearchMode){
            case WRITER:
                result = commentRepository.findAllByWriterContainingAndCreatedDateTimeBetween(keyword, from, to, page);
                break;

            case CONTENT:
                result = commentRepository.findAllByContentContainingAndCreatedDateTimeBetween(keyword, from, to, page);
                break;

            case ARTICLE:
                BoardArticle article = articleRepository.findById(Long.parseLong(keyword)).orElseThrow(NoArticleFoundException::new);
                result = commentRepository.findAllByBoardArticleAndCreatedDateTimeBetween(article, from, to, page);
                break;

            case ACCOUNT:
                BoardAccount account = accountRepository.findByUserId(keyword).orElseThrow(NoAccountFoundException::new);
                result = commentRepository.findAllByBoardAccountAndCreatedDateTimeBetween(account, from, to, page);
                break;
        }

        return new BoardCommentDTO.PagedComments(result);
    }



    // Account Services.

    @Override
    @Transactional(readOnly = true)
    public long countAllAccounts() {
        return accountRepository.count();
    }

    @Override
    public boolean authManagerAccess(String userId) {
        BoardAccount boardAccount = accountRepository.findByUserIdAndAvailableTrue(userId).orElseThrow(NoAccountFoundException::new);
        return !boardAccount.getAuthorities().isEmpty();
    }

    @Override
    public void changeManagerLevel(String userId, ManagerLevel level, AccountLevelManagementCommand.LevelOperation operation) {
        BoardAccount boardAccount = accountRepository.findByUserId(userId).orElseThrow(NoAccountFoundException::new);
        Set<ManagerLevel> managerLevels = boardAccount.getManagerLevels();
        if(operation.equals(GRANT) && !managerLevels.contains(level)) {
            accountLevelRepository.save(new BoardAccountLevel(boardAccount, level));
        }
        else if(operation.equals(REVOKE) && managerLevels.contains(level)) {
            accountLevelRepository.deleteByBoardAccountAndAccountLevel(boardAccount, level);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BoardAccountDTO.PagedAccounts readAccountsPage(int pageNum, int pageSize, LocalDateTime from, LocalDateTime to) {
        return new BoardAccountDTO.PagedAccounts(
                accountRepository.findAllByCreatedDateTimeBetween(from, to, PageRequest.of(pageNum, pageSize)));
    }

    @Override
    @Transactional(readOnly = true)
    public BoardAccountDTO.PagedAccounts searchAccountsPage(BoardAccountDTO.AccountSearchMode mode, String keyword, int pageNum, int pageSize, LocalDateTime from, LocalDateTime to) {
        Page<BoardAccount> result = Page.empty();

        switch(mode){
            case EMAIL:
                result = accountRepository.findAllByEmailContainingAndCreatedDateTimeBetween(keyword, from, to, PageRequest.of(pageNum, pageSize));
                break;

            case USERID:
                result = accountRepository.findAllByUserIdContainingAndCreatedDateTimeBetween(keyword, from, to, PageRequest.of(pageNum, pageSize));
                break;

            case USERNAME:
                result = accountRepository.findAllByUsernameContainingAndCreatedDateTimeBetween(keyword, from, to, PageRequest.of(pageNum, pageSize));
                break;
        }

        return new BoardAccountDTO.PagedAccounts(result);
    }

    @Override
    public void invalidateAccounts(List<Long> accountIds) {
        for (long accountId : accountIds) {
            accountRepository.findById(accountId).ifPresent(BoardAccount::invalidate);
        }
    }

    @Override
    public void restoreAccounts(List<Long> accountIds) {
        for (long accountId : accountIds) {
            accountRepository.findById(accountId).ifPresent(BoardAccount::restore);
        }
    }

    @Override
    public void changePassword(List<Long> accountIds, String newPassword) {
        for (long accountId : accountIds) {
            accountRepository.findById(accountId).ifPresent(boardAccount -> boardAccount.changePassword(passwordEncoder.encode(newPassword)));
        }
    }

    @Override
    public void changeUsername(List<Long> accountIds, String newUsername) {
        for (long accountId : accountIds) {
            accountRepository.findById(accountId).ifPresent(boardAccount -> boardAccount.changeUsername(newUsername));
        }
    }
}
