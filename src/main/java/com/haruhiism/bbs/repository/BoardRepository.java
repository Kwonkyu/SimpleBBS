package com.haruhiism.bbs.repository;

import com.haruhiism.bbs.domain.BoardArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends CrudRepository<BoardArticle, Long> {

    public Page<BoardArticle> findAllByOrderByBidAsc(Pageable pageable);
}
