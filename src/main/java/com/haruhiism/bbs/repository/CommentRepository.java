package com.haruhiism.bbs.repository;

import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<BoardComment, Long> {

    public Page<BoardComment> findAllByBoardArticleOrderByIdAsc(BoardArticle boardArticle, Pageable pageable);
    public Page<BoardComment> findAllByBoardArticleAndDeletedFalseOrderByIdAsc(BoardArticle boardArticle, Pageable pageable);

    public void deleteAllByBoardArticle(BoardArticle boardArticle);

    public int countAllByBoardArticle(BoardArticle boardArticle);
    public int countAllByBoardArticleAndDeletedFalse(BoardArticle boardArticle);
}
