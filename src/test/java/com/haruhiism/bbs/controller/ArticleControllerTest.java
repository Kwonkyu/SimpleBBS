package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.exception.UpdateDeletedArticleException;
import com.haruhiism.bbs.service.article.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ArticleControllerTest {

    @Autowired
    ArticleService articleService;


    @Test
    void createAndReadBoardArticleTest() throws Exception {
        // given
        String content = "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit";
        BoardArticle boardArticle = new BoardArticle("writer01", "p@ssw0rd01", "title01", content);

        // when
        articleService.createArticle(boardArticle);
        BoardArticle readArticle = articleService.readArticle(boardArticle.getArticleID());

        // then
        assertEquals(boardArticle, readArticle);
    }

    @Test
    void readInvalidArticleTest() throws Exception {
        assertThrows(NoArticleFoundException.class, () -> {
            articleService.readArticle(-1L);
        });

        assertThrows(NoArticleFoundException.class, () -> {
            articleService.readArticle(0L);
        });
    }

    @Test
    void createAndEditArticleTest() throws Exception {
        // given
        BoardArticle boardArticle = new BoardArticle("writer", "password", "edit_me_title", "edit_me_content");
        articleService.createArticle(boardArticle);

        boardArticle.setTitle("edited_title");
        boardArticle.setContent("edited_content");
        articleService.updateArticle(boardArticle);

        // when
        BoardArticle readArticle = articleService.readArticle(boardArticle.getArticleID());

        // then
        assertEquals("edited_title", readArticle.getTitle());
        assertEquals("edited_content", readArticle.getContent());

        // when
        boardArticle.setTitle("delete_title");
        boardArticle.setContent("delete_content");
        articleService.deleteArticle(boardArticle);

        // then
        assertThrows(UpdateDeletedArticleException.class, () -> articleService.updateArticle(boardArticle));
    }

    @Test
    void createAndDeleteArticleTest() throws Exception {
        // given
        BoardArticle boardArticle = new BoardArticle("writer", "password", "delete_me_title", "delete_me_content");
        articleService.createArticle(boardArticle);

        // when
        articleService.deleteArticle(boardArticle);

        // then
        assertThrows(NoArticleFoundException.class, () -> articleService.readArticle(boardArticle.getArticleID()));
    }
}