package com.haruhiism.bbs.service.comment;

import com.haruhiism.bbs.domain.dto.AuthDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentsDTO;

public interface CommentService {

    void createComment(BoardCommentDTO comment, AuthDTO authDTO);

    BoardCommentsDTO readCommentsOfArticle(Long articleID, int pageNum, int pageSize);

    BoardCommentsDTO readCommentsOfAccount(String userId, int pageNum, int pageSize);

    BoardCommentDTO readComment(Long commentID);

    void deleteComment(Long commentID, AuthDTO authDTO);

}
