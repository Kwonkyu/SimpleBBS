package com.haruhiism.bbs.service.article;

import com.haruhiism.bbs.domain.ArticleSearchMode;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;

import java.util.List;

public interface ArticleService {

    /**
     * Check article authorization with given password
     * @param articleId Article's id.
     * @param password Password to compare.
     * @return boolean value indicating authorized or not.
     */
    boolean authorizeArticleAccess(long articleId, String password);

    /**
     * Create article with anonymous user.
     * @param article BoardArticleDTO object containing article information.
     * @return id of created article.
     */
    long createArticle(BoardArticleDTO article);

    /**
     * Create article with given user.
     * @param article BoardArticleDTO object containing article information.
     * @param userId User's id.
     * @return id of created article.
     */
    long createArticle(BoardArticleDTO article, String userId);

    /**
     * Read article.
     * @param articleId Article's id.
     * @return BoardArticleDTO object containing article information.
     */
    BoardArticleDTO readArticle(long articleId);

    /**
     * Read articles by page.
     * @param pageNum Page number.
     * @param pageSize Page size.
     * @return BoardArticleDTO.PagedArticles object containing articles information.
     */
    BoardArticleDTO.PagedArticles readAllByPages(int pageNum, int pageSize);

    /**
     * Search articles by page.
     * @param articleSearchMode Search mode.
     * @param keyword Search keyword.
     * @param pageNum Page number.
     * @param pageSize Page size.
     * @return BoardArticleDTO.PagedArticles object containing searched articles information.
     */
    BoardArticleDTO.PagedArticles searchAllByPages(ArticleSearchMode articleSearchMode, String keyword, int pageNum, int pageSize);

    /**
     * Update article.
     * @param article BoardArticleDTO object containing updated article's information.
     */
    void updateArticle(BoardArticleDTO article);

    /**
     * Delete article.
     * @param articleId article's id.
     */
    void deleteArticle(long articleId);
}
