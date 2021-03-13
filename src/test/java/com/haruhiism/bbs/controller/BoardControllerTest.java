package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.domain.BoardArticle;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.exception.UpdateDeletedArticleException;
import com.haruhiism.bbs.service.BoardService.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BoardControllerTest {

    @Autowired
    BoardService boardService;


    @Test
    void createAndReadBoardArticleTest() throws Exception {
        // given
        String content = "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit";
        BoardArticle boardArticle = new BoardArticle("writer01", "p@ssw0rd01", "title01", content);

        // when
        boardService.createArticle(boardArticle);
        BoardArticle readArticle = boardService.readArticle(boardArticle.getBid());

        // then
        assertEquals(boardArticle, readArticle);
    }

    @Test
    void readInvalidArticleTest() throws Exception {
        assertThrows(NoArticleFoundException.class, () -> {
            boardService.readArticle(-1L);
        });

        assertThrows(NoArticleFoundException.class, () -> {
            boardService.readArticle(0L);
        });
    }

    @Test
    void createAndEditArticleTest() throws Exception {
        // given
        BoardArticle boardArticle = new BoardArticle("writer", "password", "edit_me_title", "edit_me_content");
        boardService.createArticle(boardArticle);

        boardArticle.setTitle("edited_title");
        boardArticle.setContent("edited_content");
        boardService.updateArticle(boardArticle);

        // when
        BoardArticle readArticle = boardService.readArticle(boardArticle.getBid());

        // then
        assertEquals("edited_title", readArticle.getTitle());
        assertEquals("edited_content", readArticle.getContent());

        // when
        boardArticle.setTitle("delete_title");
        boardArticle.setContent("delete_content");
        boardService.deleteArticle(boardArticle);

        // then
        assertThrows(UpdateDeletedArticleException.class, () -> boardService.updateArticle(boardArticle));
    }

}