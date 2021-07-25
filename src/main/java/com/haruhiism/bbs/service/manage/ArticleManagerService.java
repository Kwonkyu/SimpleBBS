package com.haruhiism.bbs.service.manage;

import java.time.LocalDateTime;
import java.util.List;

import static com.haruhiism.bbs.domain.dto.BoardArticleDTO.*;

public interface ArticleManagerService {

    // TODO: integrate and return arrays?
    /**
     * Count all articles.
     * @return Article counts.
     */
    long countAllArticles();

    /**
     * Count all deleted articles.
     * @return Article counts.
     */
    long countAllDeletedArticles();

    /**
     * Count all not deleted articles.
     * @return Article counts.
     */
    long countAllNotDeletedArticles();

    /**
     * Get articles with paging.
     * @param pageNum Page's number.
     * @param pageSize Page's size.
     * @param from LocalDateTime constraints from.
     * @param to LOcalDateTime constraints to.
     * @return BoardArticleDTO.PagedArticles object containing articles.
     */
    PagedArticles readArticlesPage(int pageNum, int pageSize, LocalDateTime from, LocalDateTime to);

    /**
     * Search articles with paging.
     * @param articleSearchMode Search mode.
     * @param keyword Search keyword.
     * @param pageNum Page's number.
     * @param pageSize Page's size.
     * @param from LocalDateTime search constraints from.
     * @param to LOcalDateTime search constraints to.
     * @return BoardArticleDTO.PagedArticles object containing searched articles.
     */
    PagedArticles searchArticlesPage(ArticleSearchMode articleSearchMode, String keyword, int pageNum, int pageSize, LocalDateTime from, LocalDateTime to);

    /**
     * Delete articles.
     * @param articleIds Article ids.
     */
    void deleteArticles(List<Long> articleIds);

    /**
     * Restore articles.
     * @param articleIds Article ids.
     */
    void restoreArticles(List<Long> articleIds);

}
