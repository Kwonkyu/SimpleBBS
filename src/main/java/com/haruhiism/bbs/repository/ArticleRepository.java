package com.haruhiism.bbs.repository;

import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ArticleRepository extends CrudRepository<BoardArticle, Long> {

    Optional<BoardArticle> findByIdAndDeletedFalse(long articleId);

    Page<BoardArticle> findAllByOrderByIdDesc(Pageable pageable);
    Page<BoardArticle> findAllByDeletedFalseOrderByIdDesc(Pageable pageable);

    Page<BoardArticle> findAllByWriterContainingOrderByIdDesc(String writer, Pageable pageable);
    Page<BoardArticle> findAllByWriterContainingAndDeletedFalseOrderByIdDesc(String writer, Pageable pageable);

    Page<BoardArticle> findAllByTitleContainingOrderByIdDesc(String title, Pageable pageable);
    Page<BoardArticle> findAllByTitleContainingAndDeletedFalseOrderByIdDesc(String title, Pageable pageable);

    Page<BoardArticle> findAllByContentContainingOrderByIdDesc(String content, Pageable pageable);
    Page<BoardArticle> findAllByContentContainingAndDeletedFalseOrderByIdDesc(String content, Pageable pageable);

    Page<BoardArticle> findAllByTitleContainingOrContentContainingOrderByIdDesc(String title, String content, Pageable pageable);
    Page<BoardArticle> findAllByTitleContainingAndDeletedFalseOrContentContainingAndDeletedFalseOrderByIdDesc(String title, String content, Pageable pageable);

    Page<BoardArticle> findAllByBoardAccount(BoardAccount boardAccount, Pageable pageable);
    Page<BoardArticle> findAllByBoardAccountAndDeletedFalse(BoardAccount boardAccount, Pageable pageable);

    Long countAllByDeletedTrue();

    // TODO: make dynamic query.
    Page<BoardArticle> findAllByCreatedDateTimeBetweenOrderByIdDesc(LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<BoardArticle> findAllByWriterContainingAndCreatedDateTimeBetweenOrderByIdDesc(String writer, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<BoardArticle> findAllByTitleContainingAndCreatedDateTimeBetweenOrderByIdDesc(String title, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<BoardArticle> findAllByContentContainingAndCreatedDateTimeBetweenOrderByIdDesc(String content, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<BoardArticle> findAllByTitleContainingAndCreatedDateTimeBetweenOrContentContainingAndCreatedDateTimeBetweenOrderByIdDesc(String title, LocalDateTime from1, LocalDateTime to1, String content, LocalDateTime from2, LocalDateTime to2, Pageable pageable);

}
