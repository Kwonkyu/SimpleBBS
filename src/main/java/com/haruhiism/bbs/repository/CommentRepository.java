package com.haruhiism.bbs.repository;

import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CommentRepository extends CrudRepository<BoardComment, Long> {

    Page<BoardComment> findAll(Pageable pageable);

    Page<BoardComment> findAllByBoardArticleOrderByIdAsc(BoardArticle boardArticle, Pageable pageable);
    Page<BoardComment> findAllByBoardArticleAndDeletedFalseOrderByIdAsc(BoardArticle boardArticle, Pageable pageable);

    Page<BoardComment> findAllByWriterContaining(String writer, Pageable pageable);
    Page<BoardComment> findAllByContentContaining(String content, Pageable pageable);

    void deleteAllByBoardArticle(BoardArticle boardArticle);

    int countAllByBoardArticle(BoardArticle boardArticle);
    int countAllByBoardArticleAndDeletedFalse(BoardArticle boardArticle);

    Page<BoardComment> findAllByBoardAccount(BoardAccount account, Pageable pageable);
    Page<BoardComment> findAllByBoardAccountAndDeletedFalse(BoardAccount account, Pageable pageable);

    Page<BoardComment> findAllByCreatedDateTimeBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    Long countAllByDeletedTrue();
}
