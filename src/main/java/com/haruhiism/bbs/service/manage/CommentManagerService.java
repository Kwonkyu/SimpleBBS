package com.haruhiism.bbs.service.manage;

import com.haruhiism.bbs.domain.CommentSearchMode;
import com.haruhiism.bbs.domain.dto.BoardCommentDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentManagerService {

    /**
     * Count all comments.
     * @return Count of comments.
     */
    long countAllComments();
    /**
     * Count all deleted comments.
     * @return Count of comments.
     */
    long countAllDeletedComments();

    /**
     * Count all not deleted comments.
     * @return Count of comments.
     */
    long countAllNotDeletedComments();

    /**
     * Read comments with paging.
     * @param pageNum Page's number.
     * @param pageSize Page's size.
     * @param from LocalDateTime constraints from.
     * @param to LocalDateTime constraints to.
     * @return BoardCommentDTO.PagedComments object containing comments.
     */
    BoardCommentDTO.PagedComments readCommentsPage(int pageNum, int pageSize, LocalDateTime from, LocalDateTime to);

    /**
     * Search comments with paging.
     * @param commentSearchMode Search mode.
     * @param keyword Search keyword.
     * @param pageNum Page's number.
     * @param pageSize Page's size.
     * @param from LocalDateTime constraints from.
     * @param to LocalDateTime constraints to.
     * @return BoardCommentDTO.PagedComments object containing searched comments.
     */
    BoardCommentDTO.PagedComments searchCommentsPage(CommentSearchMode commentSearchMode, String keyword, int pageNum, int pageSize, LocalDateTime from, LocalDateTime to);

    /**
     * Delete comments.
     * @param commentIds List of comment ids.
     */
    void deleteComments(List<Long> commentIds);

    /**
     * Restore comments.
     * @param commentIds  List of comment ids.
     */
    void restoreComments(List<Long> commentIds);

}
