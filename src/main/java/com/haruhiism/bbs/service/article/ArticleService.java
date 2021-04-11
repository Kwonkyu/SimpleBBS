package com.haruhiism.bbs.service.article;

import com.haruhiism.bbs.domain.SearchMode;
import com.haruhiism.bbs.domain.dto.BoardArticleAuthDTO;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;
import com.haruhiism.bbs.service.authentication.LoginSessionInfo;

import java.util.Optional;

public interface ArticleService {

    void createArticle(BoardArticleDTO article, LoginSessionInfo loginSessionInfo);

    BoardArticleDTO readArticle(Long articleID);

    BoardArticlesDTO readAllByPages(int pageNum, int pageSize);

    BoardArticlesDTO searchAllByPages(SearchMode searchMode, String keyword, int pageNum, int pageSize);

    void updateArticle(BoardArticleDTO article, LoginSessionInfo loginSessionInfo);

    void deleteArticle(BoardArticleAuthDTO article, LoginSessionInfo loginSessionInfo);

    Optional<BoardArticleDTO> authArticleAccess(BoardArticleAuthDTO articleAuthDTO);
}
