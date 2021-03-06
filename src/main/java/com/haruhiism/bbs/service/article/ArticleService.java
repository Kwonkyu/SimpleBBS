package com.haruhiism.bbs.service.article;

import com.haruhiism.bbs.domain.ArticleSearchMode;
import com.haruhiism.bbs.domain.dto.AuthDTO;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;

import java.util.Optional;

public interface ArticleService {

    Long createArticle(BoardArticleDTO article, AuthDTO authDTO);

    BoardArticleDTO readArticle(Long articleId);

    BoardArticlesDTO readAllByPages(int pageNum, int pageSize);

    BoardArticlesDTO searchAllByPages(ArticleSearchMode articleSearchMode, String keyword, int pageNum, int pageSize);

    BoardArticlesDTO readArticlesOfAccount(String userId, int pageNum, int pageSize);

    void updateArticle(BoardArticleDTO article, AuthDTO authDTO);

    void deleteArticle(Long articleId, AuthDTO authDTO);

    Optional<BoardArticleDTO> authArticleEdit(Long articleId, AuthDTO articleAuthDTO);

}
