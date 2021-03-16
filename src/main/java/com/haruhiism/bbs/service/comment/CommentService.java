package com.haruhiism.bbs.service.comment;

import com.haruhiism.bbs.domain.entity.BoardComment;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {

    public void createComment(BoardComment comment);

    public BoardComment readComment(Long commentID);

    public List<BoardComment> readCommentsOfArticle(Long articleID);

    public Page<BoardComment> readCommentsOfArticleByPages(Long articleID, int pageNum, int pageSize);

    public void updateComment(BoardComment comment);

    public void deleteComment(Long commentID);

    public void deleteComment(BoardComment comment);

    public boolean authCommentAccess(Long commentID, String rawPassword);
}
