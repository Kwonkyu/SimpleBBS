package com.haruhiism.bbs.repository;

import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<BoardComment, Long> {

    public List<BoardComment> findAllByArticleID(Long articleID);

    public Page<BoardComment> findByArticleIDOrderByCommentID(Long articleID, Pageable pageable);
}
