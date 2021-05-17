package com.haruhiism.bbs.repository;

import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ArticleRepository extends CrudRepository<BoardArticle, Long> {

    public Page<BoardArticle> findAllByOrderByIdDesc(Pageable pageable);
    public Page<BoardArticle> findAllByDeletedFalseOrderByIdDesc(Pageable pageable);

    public Page<BoardArticle> findAllByWriterContainingOrderByIdDesc(String writer, Pageable pageable);
    public Page<BoardArticle> findAllByWriterContainingAndDeletedFalseOrderByIdDesc(String writer, Pageable pageable);

    public Page<BoardArticle> findAllByTitleContainingOrderByIdDesc(String title, Pageable pageable);
    public Page<BoardArticle> findAllByTitleContainingAndDeletedFalseOrderByIdDesc(String title, Pageable pageable);

    public Page<BoardArticle> findAllByContentContainingOrderByIdDesc(String content, Pageable pageable);
    public Page<BoardArticle> findAllByContentContainingAndDeletedFalseOrderByIdDesc(String content, Pageable pageable);

    public Page<BoardArticle> findAllByTitleContainingOrContentContainingOrderByIdDesc(String title, String content, Pageable pageable);
    public Page<BoardArticle> findAllByTitleContainingAndDeletedFalseOrContentContainingAndDeletedFalseOrderByIdDesc(String title, String content, Pageable pageable);

    public Page<BoardArticle> findAllByBoardAccount(BoardAccount boardAccount, Pageable pageable);
    public Page<BoardArticle> findAllByBoardAccountAndDeletedFalse(BoardAccount boardAccount, Pageable pageable);

    public Long countAllByDeletedTrue();


    public Page<BoardArticle> findAllByCreatedDateTimeBetweenOrderByIdDesc(LocalDateTime from, LocalDateTime to, Pageable pageable);
    public Page<BoardArticle> findAllByWriterContainingAndCreatedDateTimeBetweenOrderByIdDesc(String writer, LocalDateTime from, LocalDateTime to, Pageable pageable);
    public Page<BoardArticle> findAllByTitleContainingAndCreatedDateTimeBetweenOrderByIdDesc(String title, LocalDateTime from, LocalDateTime to, Pageable pageable);
    public Page<BoardArticle> findAllByContentContainingAndCreatedDateTimeBetweenOrderByIdDesc(String content, LocalDateTime from, LocalDateTime to, Pageable pageable);
    public Page<BoardArticle> findAllByTitleContainingAndCreatedDateTimeBetweenOrContentContainingAndCreatedDateTimeBetweenOrderByIdDesc(String title, LocalDateTime from1, LocalDateTime to1, String content, LocalDateTime from2, LocalDateTime to2, Pageable pageable);

}
