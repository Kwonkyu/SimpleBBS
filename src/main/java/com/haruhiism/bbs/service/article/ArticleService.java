package com.haruhiism.bbs.service.article;

import com.haruhiism.bbs.domain.SearchMode;
import com.haruhiism.bbs.domain.dto.BoardArticleAuthDTO;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;
import com.haruhiism.bbs.service.authentication.LoginSessionInfo;

import java.util.Optional;

public interface ArticleService {

    void createArticle(BoardArticleDTO article, BoardArticleAuthDTO authDTO);

    BoardArticleDTO readArticle(Long articleId);

    BoardArticlesDTO readAllByPages(int pageNum, int pageSize);

    BoardArticlesDTO searchAllByPages(SearchMode searchMode, String keyword, int pageNum, int pageSize);

    void updateArticle(BoardArticleDTO article, BoardArticleAuthDTO authDTO);

    void deleteArticle(Long articleId, BoardArticleAuthDTO authDTO);

    Optional<BoardArticleDTO> authArticleEdit(Long articleId, BoardArticleAuthDTO articleAuthDTO);
}
