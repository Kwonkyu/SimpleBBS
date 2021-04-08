package com.haruhiism.bbs.service.comment;

import com.haruhiism.bbs.domain.dto.BoardCommentDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentsDTO;

public interface CommentService {

    public void createComment(BoardCommentDTO comment);

    public BoardCommentsDTO readCommentsOfArticle(Long articleID, int pageNum, int pageSize);

    public BoardCommentDTO readComment(Long commentID);

    public void updateComment(BoardCommentDTO comment);

    public void deleteComment(Long commentID);

    public void deleteCommentsOfArticle(Long articleID);

    public boolean authCommentAccess(Long commentID, String rawPassword);
}
