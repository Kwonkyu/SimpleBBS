package com.haruhiism.bbs.service.manage;

import com.haruhiism.bbs.domain.ArticleSearchMode;
import com.haruhiism.bbs.domain.CommentSearchMode;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentsDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentManagerService {

    Long countAllComments();
    Long countAllDeletedComments();
    Long countAllNotDeletedComments();

    BoardCommentsDTO readComments(int pageNum, int pageSize);

    void deleteComments(List<Long> commentIds);
    void restoreComments(List<Long> commentIds);

    BoardCommentsDTO searchCommentsByPages(CommentSearchMode commentSearchMode, String keyword, int pageNum, int pageSize);
    BoardCommentsDTO searchCommentsBetweenDateByPages(CommentSearchMode commentSearchMode, String keyword, LocalDateTime from, LocalDateTime to, int pageNum, int pageSize);
}
