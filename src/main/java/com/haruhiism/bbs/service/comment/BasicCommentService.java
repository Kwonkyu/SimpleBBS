package com.haruhiism.bbs.service.comment;

import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import com.haruhiism.bbs.exception.comment.NoCommentFoundException;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.haruhiism.bbs.service.RepositoryUtility.*;
import static com.haruhiism.bbs.domain.dto.BoardCommentDTO.*;


@Service
@Transactional
@RequiredArgsConstructor
public class BasicCommentService implements CommentService {

    private final AccountRepository accountRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    public boolean authorizeCommentAccess(long commentId, String password) {
        BoardComment commentById = findCommentById(commentRepository, commentId);
        return passwordEncoder.matches(password, commentById.getPassword());
    }

    @Override
    public long createComment(Submit commentDTO) {
        commentDTO.encodePassword(passwordEncoder);
        BoardArticle article = findArticleById(articleRepository, commentDTO.getArticleId());
        BoardComment comment = new BoardComment(article, commentDTO);
        commentRepository.save(comment);
        return comment.getId();
    }

    @Override
    public long createComment(Submit commentDTO, String userId) {
        commentDTO.encodePassword(passwordEncoder, UUID.randomUUID().toString());
        BoardAccount account = findAccountByUserId(accountRepository, userId);
        BoardArticle article = findArticleById(articleRepository, commentDTO.getArticleId());
        BoardComment comment = new BoardComment(article, account, commentDTO);
        commentRepository.save(comment);
        return comment.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedComments readArticleCommentsPaged(long articleID, int pageNum, int pageSize){
        Page<BoardComment> boardComments = findCommentsByArticle(
                commentRepository,
                findArticleById(articleRepository, articleID),
                pageNum, pageSize);

        return new PagedComments(boardComments);
    }

    @Override
    public PagedComments readCommentsOfAccount(String userId, int pageNum, int pageSize) {
        Page<BoardComment> comments = findCommentsByAccount(
                commentRepository,
                findAccountByUserId(accountRepository, userId),
                pageNum, pageSize);

        return new PagedComments(comments);
    }

    @Override
    @Transactional(readOnly = true)
    public Read readComment(long commentId) {
        BoardComment boardComment = findCommentById(commentRepository, commentId);
        if(boardComment.isDeleted()){
            throw new NoCommentFoundException();
        }

        return new Read(boardComment);
    }

    @Override
    public long deleteComment(long commentId) {
        BoardComment comment = findCommentById(commentRepository, commentId);
        comment.delete();
        return comment.getBoardArticle().getId();
    }

}
