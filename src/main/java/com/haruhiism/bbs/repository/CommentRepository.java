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

    public List<BoardComment> findAllByBoardAccount(BoardAccount boardAccount);

    public Page<BoardComment> findAllByBoardArticleOrderByIdAsc(BoardArticle boardArticle, Pageable pageable);

    public void deleteAllByBoardArticle(BoardArticle boardArticle);
}
