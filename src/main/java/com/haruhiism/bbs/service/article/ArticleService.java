package com.haruhiism.bbs.service.article;

import com.haruhiism.bbs.domain.SearchMode;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;
import com.haruhiism.bbs.service.authentication.LoginSessionInfo;

import java.util.Optional;

public interface ArticleService {

    void createArticle(BoardArticleDTO article, LoginSessionInfo loginSessionInfo);

    BoardArticleDTO readArticle(Long articleID);

    BoardArticlesDTO readAllByPages(int pageNum, int pageSize);

    BoardArticlesDTO searchAllByPages(SearchMode searchMode, String keyword, int pageNum, int pageSize);

    void updateArticle(BoardArticleDTO boardArticle);

    void deleteArticle(Long articleID);

    /**
     * Get entity by PK articleID, compare password with V rawPassword.
     * @param articleId Primary Key to get entity from DB.
     * @param rawPassword Password to compare with entity's password.
     * @return Returns true if given raw password matches entity's decoded password else false.
     */
    Optional<BoardArticleDTO> authArticleAccess(Long articleId, String rawPassword);

    Optional<BoardArticleDTO> authArticleAccess(Long articleId, Long accountId);
}
