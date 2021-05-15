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

    Page<BoardArticle> findAllByCreatedDateTimeBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    public Long countAllByDeletedTrue();
}
