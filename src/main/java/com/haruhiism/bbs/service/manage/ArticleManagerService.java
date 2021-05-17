package com.haruhiism.bbs.service.manage;

import com.haruhiism.bbs.domain.ArticleSearchMode;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleManagerService {

    Long countAllArticles();
    Long countAllDeletedArticles();
    Long countAllNotDeletedArticles();

    BoardArticlesDTO readArticles(int pageNum, int pageSize, LocalDateTime from, LocalDateTime to);
    BoardArticlesDTO searchArticles(ArticleSearchMode articleSearchMode, String keyword, int pageNum, int pageSize, LocalDateTime from, LocalDateTime to);

    void deleteArticles(List<Long> articleIds);
    void restoreArticles(List<Long> articleIds);

}
