package com.haruhiism.bbs.service.manage;

import com.haruhiism.bbs.domain.ArticleSearchMode;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleManagerService {

    Long countAllArticles();
    Long countAllDeletedArticles();
    Long countAllNotDeletedArticles();

    BoardArticlesDTO readArticles(int pageNum, int pageSize);

    void deleteArticles(List<Long> articleIds);
    void restoreArticles(List<Long> articleIds);

    BoardArticlesDTO searchArticlesByPages(ArticleSearchMode articleSearchMode, String keyword, int pageNum, int pageSize);
    BoardArticlesDTO searchArticlesBetweenDateByPages(ArticleSearchMode articleSearchMode, String keyword, LocalDateTime from, LocalDateTime to, int pageNum, int pageSize);
}
