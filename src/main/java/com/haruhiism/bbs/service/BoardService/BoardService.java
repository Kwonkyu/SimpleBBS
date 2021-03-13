package com.haruhiism.bbs.service.BoardService;

import com.haruhiism.bbs.domain.BoardArticle;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BoardService {

    public void createArticle(BoardArticle article);

    public BoardArticle readArticle(Long articleID);

    public List<BoardArticle> readAll();

    public Page<BoardArticle> readAllByPages(int pageNum, int pageSize);

    public void updateArticle(BoardArticle boardArticle);

    public void deleteArticle(Long articleID);

    public void deleteArticle(BoardArticle boardArticle);

    /**
     * Get entity by PK bid, compare password with V rawPassword.
     * @param bid Primary Key to get entity from DB.
     * @param rawPassword Password to compare with entity's password.
     * @return Returns true if given raw password matches entity's decoded password else false.
     */
    public boolean authEntityAccess(Long bid, String rawPassword);
}
