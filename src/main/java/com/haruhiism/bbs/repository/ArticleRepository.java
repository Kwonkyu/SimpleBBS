package com.haruhiism.bbs.repository;

import com.haruhiism.bbs.domain.entity.BoardArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends CrudRepository<BoardArticle, Long> {

    public Page<BoardArticle> findAllByOrderByArticleIDDesc(Pageable pageable);

    public void deleteByArticleID(Long articleID);
}
