package com.haruhiism.bbs.service.manage;

import com.haruhiism.bbs.domain.CommentSearchMode;
import com.haruhiism.bbs.domain.dto.BoardCommentsDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentManagerService {

    Long countAllComments();
    Long countAllDeletedComments();
    Long countAllNotDeletedComments();

    BoardCommentsDTO readComments(int pageNum, int pageSize, LocalDateTime from, LocalDateTime to);
    BoardCommentsDTO searchComments(CommentSearchMode commentSearchMode, String keyword, int pageNum, int pageSize, LocalDateTime from, LocalDateTime to);

    void deleteComments(List<Long> commentIds);
    void restoreComments(List<Long> commentIds);

}
