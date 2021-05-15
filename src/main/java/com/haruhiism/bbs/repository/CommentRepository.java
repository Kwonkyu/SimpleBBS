package com.haruhiism.bbs.repository;

import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
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

    Page<BoardComment> findAllByCreatedDateTimeBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
    // https://stackoverflow.com/a/56161777/10242688
    // TODO: performance issue?
    @Query("SELECT comment FROM BoardComment comment WHERE comment.writer LIKE %?1% and comment.createdDateTime>=?2 and comment.createdDateTime<=?3 " +
           "OR comment.boardAccount.username LIKE %?1% and comment.createdDateTime>=?2 and comment.createdDateTime<=?3")
    Page<BoardComment> findAllByWriterContainingAndCreatedDateTimeBetween(String writer, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<BoardComment> findAllByContentContainingAndCreatedDateTimeBetween(String content, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<BoardComment> findAllByBoardArticleAndCreatedDateTimeBetween(BoardArticle boardArticle, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<BoardComment> findAllByBoardAccountAndCreatedDateTimeBetween(BoardAccount boardAccount, LocalDateTime from, LocalDateTime to, Pageable pageable);


    Long countAllByDeletedTrue();
}
