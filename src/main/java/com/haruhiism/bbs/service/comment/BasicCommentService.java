package com.haruhiism.bbs.service.comment;

import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.dto.AuthDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentsDTO;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import com.haruhiism.bbs.exception.account.NoAccountFoundException;
import com.haruhiism.bbs.exception.article.NoArticleFoundException;
import com.haruhiism.bbs.exception.auth.AuthenticationFailedException;
import com.haruhiism.bbs.exception.comment.NoCommentFoundException;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.repository.CommentRepository;
import com.haruhiism.bbs.service.DataEncoder.DataEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicCommentService implements CommentService {

    private final AccountRepository accountRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    private final DataEncoder dataEncoder;

    @Override
    public void createComment(BoardCommentDTO commentDTO, AuthDTO authDTO) {
        BoardArticle commentedArticle = articleRepository
                .findById(commentDTO.getArticleId())
                .orElseThrow(NoArticleFoundException::new);

        if (commentedArticle.isDeleted()) {
            throw new NoArticleFoundException();
        }

        BoardComment boardComment = new BoardComment(
                commentDTO.getWriter(),
                dataEncoder.encode(commentDTO.getPassword()),
                commentDTO.getContent(),
                commentedArticle);

        LoginSessionInfo loginSessionInfo = authDTO.getLoginSessionInfo();
        if(loginSessionInfo != null){
            BoardAccount boardAccount = accountRepository.findById(loginSessionInfo.getAccountID())
                    .orElseThrow(NoAccountFoundException::new);

            boardComment.registerCommentWriter(boardAccount);
        }

        commentRepository.save(boardComment);
    }

    private BoardCommentsDTO convertPageResultToBoardCommentsDTO(Page<BoardComment> result, int pageNum){
        List<BoardCommentDTO> comments = result.get().map(BoardCommentDTO::new).collect(Collectors.toList());
        return BoardCommentsDTO.builder()
                .boardComments(comments)
                .currentPage(pageNum)
                .totalPages(result.getTotalPages()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public BoardCommentsDTO readCommentsOfArticle(Long articleID, int pageNum, int pageSize){
        Page<BoardComment> boardComments = commentRepository.findAllByBoardArticleAndDeletedFalseOrderByIdAsc(
                articleRepository.findById(articleID).orElseThrow(NoArticleFoundException::new),
                PageRequest.of(pageNum, pageSize));

        return convertPageResultToBoardCommentsDTO(boardComments, pageNum);
    }

    @Override
    public BoardCommentsDTO readCommentsOfAccount(String userId, int pageNum, int pageSize) {
        BoardAccount account = accountRepository.findByUserIdAndAvailableTrue(userId).orElseThrow(NoAccountFoundException::new);
        Page<BoardComment> comments = commentRepository.findAllByBoardAccountAndDeletedFalse(account, PageRequest.of(pageNum, pageSize));

        return convertPageResultToBoardCommentsDTO(comments, pageNum);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardCommentDTO readComment(Long commentID) {
        BoardComment boardComment = commentRepository.findById(commentID).orElseThrow(NoCommentFoundException::new);
        if(boardComment.isDeleted()){
            throw new NoCommentFoundException();
        }

        return new BoardCommentDTO(boardComment);
    }

    private boolean authCommentDelete(Long commentId, AuthDTO authDTO){
        BoardComment boardComment = commentRepository.findById(commentId).orElseThrow(NoCommentFoundException::new);
        if (boardComment.isDeleted()) {
            throw new NoCommentFoundException();
        }

        if (boardComment.isWrittenByLoggedInAccount()) {
            LoginSessionInfo loginSessionInfo = authDTO.getLoginSessionInfo();
            return loginSessionInfo != null &&
                    boardComment.getBoardAccount().getId().equals(loginSessionInfo.getAccountID());
        } else {
            return dataEncoder.compare(authDTO.getRawPassword(), boardComment.getPassword());
        }
    }

    @Override
    public void deleteComment(Long commentID, AuthDTO authDTO) {
        if (authCommentDelete(commentID, authDTO)) {
            BoardComment comment = commentRepository.findById(commentID).orElseThrow(NoCommentFoundException::new);
            comment.delete();
        } else {
            throw new AuthenticationFailedException();
        }
    }

}
