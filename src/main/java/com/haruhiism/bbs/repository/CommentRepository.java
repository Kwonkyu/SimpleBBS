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

    Page<BoardComment> findAllByBoardArticleAndDeletedFalseOrderByIdAsc(BoardArticle boardArticle, Pageable pageable);

    void deleteAllByBoardArticle(BoardArticle boardArticle);

    int countAllByBoardArticle(BoardArticle boardArticle);
    int countAllByBoardArticleAndDeletedFalse(BoardArticle boardArticle);

    Page<BoardComment> findAllByBoardAccount(BoardAccount account, Pageable pageable);
    Page<BoardComment> findAllByBoardAccountAndDeletedFalse(BoardAccount account, Pageable pageable);

    Page<BoardComment> findAllByCreatedDateTimeBetweenOrderByIdDesc(LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<BoardComment> findAllByWriterContainingAndCreatedDateTimeBetween(String writer, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<BoardComment> findAllByContentContainingAndCreatedDateTimeBetween(String content, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<BoardComment> findAllByBoardArticleAndCreatedDateTimeBetween(BoardArticle boardArticle, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<BoardComment> findAllByBoardAccountAndCreatedDateTimeBetween(BoardAccount boardAccount, LocalDateTime from, LocalDateTime to, Pageable pageable);


    Long countAllByDeletedTrue();
}
