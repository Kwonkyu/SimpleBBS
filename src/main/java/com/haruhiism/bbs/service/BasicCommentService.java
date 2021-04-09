package com.haruhiism.bbs.service;

import com.haruhiism.bbs.domain.dto.BoardCommentDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentsDTO;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.exception.NoCommentFoundException;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.repository.CommentRepository;
import com.haruhiism.bbs.service.DataEncoder.DataEncoder;
import com.haruhiism.bbs.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
    public void createComment(BoardCommentDTO comment) {
        BoardArticle commentedArticle = articleRepository
                .findById(comment.getArticleID())
                .orElseThrow(NoArticleFoundException::new);

        BoardComment boardComment = new BoardComment(
                comment.getWriter(),
                dataEncoder.encode(comment.getPassword()),
                comment.getContent(),
                commentedArticle);

        if(comment.getAccountID() != null){
            accountRepository.findById(comment.getAccountID()).ifPresent(boardComment::setCommentWriter);
        }

        commentRepository.save(boardComment);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardCommentsDTO readCommentsOfArticle(Long articleID, int pageNum, int pageSize){
        Page<BoardComment> boardComments = commentRepository.findAllByBoardArticleOrderByIdAsc(
                articleRepository.findById(articleID)
                        .orElseThrow(NoArticleFoundException::new),
                PageRequest.of(pageNum, pageSize));

        List<BoardCommentDTO> comments = boardComments.get().map(
                boardComment -> {
                    BoardCommentDTO boardCommentDTO = new BoardCommentDTO(
                            boardComment.getBoardArticle().getId(),
                            boardComment.getWriter(),
                            boardComment.getPassword(),
                            boardComment.getContent());

                    boardCommentDTO.setCommentId(boardComment.getId());
                    return boardCommentDTO;
                })
                .collect(Collectors.toList());

        return new BoardCommentsDTO(comments, pageNum, boardComments.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public BoardCommentDTO readComment(Long commentID) {
        Optional<BoardComment> boardComment = commentRepository.findById(commentID);
        if(boardComment.isEmpty()){
            throw new NoCommentFoundException();
        }

        // TODO: DTO의 생성자로 Entity 객체들을 전달해서 생성하는 방법으로 변경?
        BoardComment comment = boardComment.get();
        return new BoardCommentDTO(
                commentID,
                comment.getWriter(),
                comment.getPassword(),
                comment.getContent());
    }

    @Override
    public void updateComment(BoardCommentDTO comment) {

    }

    @Override
    public void deleteComment(Long commentID) {
        if(commentRepository.existsById(commentID)) {
            commentRepository.deleteById(commentID);
        } else {
            throw new NoCommentFoundException();
        }
    }

    @Override
    public void deleteCommentsOfArticle(Long articleID) {
        commentRepository.deleteAllByBoardArticle(articleRepository.findById(articleID).orElseThrow(NoArticleFoundException::new));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authCommentAccess(Long commentID, String rawPassword){
        Optional<BoardComment> comment = commentRepository.findById(commentID);
        if (comment.isEmpty()) {
            throw new NoCommentFoundException();
        }

        return dataEncoder.compare(rawPassword, comment.get().getPassword());
    }
}
