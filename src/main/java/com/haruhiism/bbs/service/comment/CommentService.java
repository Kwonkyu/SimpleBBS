package com.haruhiism.bbs.service.comment;

import com.haruhiism.bbs.domain.dto.BoardCommentDTO;

import java.util.List;

public interface CommentService {

    /**
     * Authorize comment access with given password.
     * @param commentId Comment's id.
     * @param password Password.
     * @return boolean value indicating authorized or not.
     */
    boolean authorizeCommentAccess(long commentId, String password);

    /**
     * Create comment with anonymous user.
     * @param comment BoardCommentDTO object containing comment information.
     * @return Created comment's id.
     */
    long createComment(BoardCommentDTO comment);

    /**
     * Create comment with given user.
     * @param comment BoardCommentDTO object containing comment information.
     * @param userId User's id.
     * @return Created comment's id.
     */
    long createComment(BoardCommentDTO comment, String userId);

    /**
     * Read comments of article with paging.
     * @param articleID Article's id.
     * @param pageNum Page's number.
     * @param pageSize Page's size.
     * @return BoardCommentDTO.PagedComments object containing comments.
     */
    BoardCommentDTO.PagedComments readArticleCommentsPaged(long articleID, int pageNum, int pageSize);

    /**
     * Read comments of account with paging.
     * @param userId User's id.
     * @param pageNum Page's number.
     * @param pageSize Page's size.
     * @return BoardCommentDTO.PagedCOmments object containing searched comments.
     */
    BoardCommentDTO.PagedComments readCommentsOfAccount(String userId, int pageNum, int pageSize);

    /**
     * Read specific comment.
     * @param commentID Comment's id.
     * @return BoardCommentDTO object containing comment information.
     */
    BoardCommentDTO readComment(long commentID);

    /**
     * Delete comment.
     * @param commentID Comment's id.
     */
    void deleteComment(long commentID);

}
