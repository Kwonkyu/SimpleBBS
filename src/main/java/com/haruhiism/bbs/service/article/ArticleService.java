package com.haruhiism.bbs.service.article;

import com.haruhiism.bbs.domain.entity.BoardArticle;
import org.springframework.data.domain.Page;

public interface ArticleService {

    public void createArticle(BoardArticle ...articles);

    public BoardArticle readArticle(Long articleID);

    public Page<BoardArticle> readAllByPages(int pageNum, int pageSize);

    public Page<BoardArticle> readAllByWriterByPages(String writer, int pageNum, int pageSize);

    public Page<BoardArticle> readAllByTitleByPages(String title, int pageNum, int pageSize);

    public Page<BoardArticle> readAllByContentByPages(String content, int pageNum, int pageSize);

    public Page<BoardArticle> readAllByTitleOrContentByPages(String keyword, int pageNum, int pageSize);

    public void updateArticle(BoardArticle boardArticle);

    public void deleteArticle(Long articleID);

    public void deleteArticle(BoardArticle boardArticle);

    /**
     * Get entity by PK articleID, compare password with V rawPassword.
     * @param articleID Primary Key to get entity from DB.
     * @param rawPassword Password to compare with entity's password.
     * @return Returns true if given raw password matches entity's decoded password else false.
     */
    public boolean authArticleAccess(Long articleID, String rawPassword);
}
