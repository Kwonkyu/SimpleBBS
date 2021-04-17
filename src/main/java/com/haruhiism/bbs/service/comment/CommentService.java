package com.haruhiism.bbs.service.comment;

import com.haruhiism.bbs.domain.dto.AuthDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentsDTO;

import java.util.Optional;

public interface CommentService {

    public void createComment(BoardCommentDTO comment, AuthDTO authDTO);

    public BoardCommentsDTO readCommentsOfArticle(Long articleID, int pageNum, int pageSize);

    public BoardCommentDTO readComment(Long commentID);

    public void deleteComment(Long commentID, AuthDTO authDTO);
}
