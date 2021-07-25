package com.haruhiism.bbs.service.article;

import static com.haruhiism.bbs.domain.dto.BoardArticleDTO.*;

public interface ArticleService {

    /**
     * Check article authorization with given password
     * @param articleId Article's id.
     * @param password Password to compare.
     * @return boolean value indicating authorized or not.
     */
    boolean authorizeAnonymousArticleAccess(long articleId, String password);

    /**
     * Create article with anonymous user.
     * @param article BoardArticleDTO object containing article information.
     * @return id of created article.
     */
    long createArticle(Submit article);

    /**
     * Create article with given user.
     * @param article BoardArticleDTO object containing article information.
     * @param userId User's id.
     * @return id of created article.
     */
    long createArticle(Submit article, String userId);

    /**
     * Read article.
     * @param articleId Article's id.
     * @return BoardArticleDTO object containing article information.
     */
    Read readArticle(long articleId);

    /**
     * Read articles by page.
     * @param pageNum Page number.
     * @param pageSize Page size.
     * @return BoardArticleDTO.PagedArticles object containing articles information.
     */
    PagedArticles readAllByPages(int pageNum, int pageSize);

    /**
     * Search articles by page.
     * @param articleSearchMode Search mode.
     * @param keyword Search keyword.
     * @param pageNum Page number.
     * @param pageSize Page size.
     * @return BoardArticleDTO.PagedArticles object containing searched articles information.
     */
    PagedArticles searchAllByPages(ArticleSearchMode articleSearchMode, String keyword, int pageNum, int pageSize);

    /**
     * Update article.
     * @param article BoardArticleDTO object containing updated article's information.
     */
    void updateArticle(Submit article);

    /**
     * Delete article.
     * @param articleId article's id.
     */
    void deleteArticle(long articleId);
}
