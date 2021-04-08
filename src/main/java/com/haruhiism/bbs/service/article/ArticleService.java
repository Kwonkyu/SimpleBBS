package com.haruhiism.bbs.service.article;

import com.haruhiism.bbs.domain.SearchMode;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;

public interface ArticleService {

    void createArticle(BoardArticleDTO... articles);

    BoardArticleDTO readArticle(Long articleID);

    BoardArticlesDTO readAllByPages(int pageNum, int pageSize);

    BoardArticlesDTO searchAllByPages(SearchMode searchMode, String keyword, int pageNum, int pageSize);

    void updateArticle(BoardArticleDTO boardArticle);

    void deleteArticle(Long articleID);

    /**
     * Get entity by PK articleID, compare password with V rawPassword.
     * @param articleID Primary Key to get entity from DB.
     * @param rawPassword Password to compare with entity's password.
     * @return Returns true if given raw password matches entity's decoded password else false.
     */
    boolean authArticleAccess(Long articleID, String rawPassword);
}
