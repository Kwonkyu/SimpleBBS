package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.exception.UpdateDeletedArticleException;
import com.haruhiism.bbs.service.article.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class ArticleControllerTest {

    @Autowired
    ArticleService articleService;


    @Test
    void createAndReadBoardArticleTest() {
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
    void readInvalidArticleTest() {
        assertThrows(NoArticleFoundException.class, () -> articleService.readArticle(-1L));

        assertThrows(NoArticleFoundException.class, () -> articleService.readArticle(0L));
    }

    @Test
    void createAndEditArticleTest() {
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
    void createAndDeleteArticleTest() {
        // given
        BoardArticle boardArticle = new BoardArticle("writer", "password", "delete_me_title", "delete_me_content");
        articleService.createArticle(boardArticle);

        // when
        articleService.deleteArticle(boardArticle);

        // then
        assertThrows(NoArticleFoundException.class, () -> articleService.readArticle(boardArticle.getArticleID()));
    }

    @Test
    void searchArticleTest() {
        // given
        BoardArticle boardArticle1 = new BoardArticle("Jason", "password", "How was your today", "I was fine.");
        BoardArticle boardArticle2 = new BoardArticle("Alva", "password", "Zullie where are you?", "Oh Zullie, oh...");
        BoardArticle boardArticle3 = new BoardArticle("Zullie", "password", "Alva where are you?", "Oh Alva, oh...");
        BoardArticle boardArticle4 = new BoardArticle("Writer", "password", "Hello World", "Cruel World");
        BoardArticle boardArticle5 = new BoardArticle("Writer", "password", "Cruel World", "Hello World");
        articleService.createArticle(boardArticle1, boardArticle2, boardArticle3, boardArticle4, boardArticle5);

        Map<String, Long> writerTestValue = new HashMap<>();
        writerTestValue.put("Writer", 2L);
        writerTestValue.put("Alva", 1L);
        writerTestValue.put("Zullie", 1L);
        writerTestValue.put("Jason", 1L);
        writerTestValue.put("e", 3L);
        writerTestValue.put("a", 2L);

        writerTestValue.forEach((target, count) -> {
            // when
            Page<BoardArticle> articles = articleService.readAllByWriterByPages(target, 0, 10);

            // then
            assertEquals(count, articles.getTotalElements());
            articles.get().map(BoardArticle::getWriter).forEach(writer -> {
                assertTrue(writer.contains(target) || writer.contains(target.toUpperCase()) || writer.contains(target.toLowerCase()),
                        String.format("Expected writer '%s' to contain '%s' but it doesn't.", writer, target));
            });
        });


        Map<String, Long> titleTestValue = new HashMap<>();
        titleTestValue.put("Zullie", 1L);
        titleTestValue.put("Alva", 1L);
        titleTestValue.put("where are you?", 2L);
        titleTestValue.put("day", 1L);
        titleTestValue.put("World", 2L);
        titleTestValue.put("h", 4L);

        titleTestValue.forEach((target, count) -> {
            // when
            Page<BoardArticle> articles = articleService.readAllByTitleByPages(target, 0, 10);

            // then
            assertEquals(count, articles.getTotalElements());
            articles.get().map(BoardArticle::getTitle).forEach(title -> {
                assertTrue(title.contains(target) || title.contains(target.toUpperCase()) || title.contains(target.toLowerCase()),
                        String.format("Expected title '%s' to contain '%s' but it doesn't.", title, target));
            });
        });


        // given
        Map<String, Long> contentTestValue = new HashMap<>();
        contentTestValue.put("oh", 2L);
        contentTestValue.put("World", 2L);
        contentTestValue.put("fine", 1L);
        contentTestValue.put("e", 4L);
        contentTestValue.put("o", 4L);
        contentTestValue.put("ll", 2L);

        contentTestValue.forEach((target, count) -> {
            // when
            Page<BoardArticle> articles = articleService.readAllByContentByPages(target, 0, 10);

            // then
            assertEquals(count, articles.getTotalElements());
            articles.get().map(BoardArticle::getContent).forEach(content -> {
                assertTrue(content.contains(target) || content.contains(target.toUpperCase()) || content.contains(target.toLowerCase()),
                        String.format("Expected content '%s' to contain '%s' but it doesn't.", content, target));
            });
        });


        // given
        Map<String, Long> titleOrContentTestValue = new HashMap<>();
        titleOrContentTestValue.put("oh", 2L);
        titleOrContentTestValue.put("you", 3L);
        titleOrContentTestValue.put("Hello", 2L);
        titleOrContentTestValue.put("Cruel", 2L);

        titleOrContentTestValue.forEach((target, count) -> {
            // when
            Page<BoardArticle> articles = articleService.readAllByTitleOrContentByPages(target, 0, 10);

            // then
            assertEquals(count, articles.getTotalElements());
            articles.get().forEach(article -> {
                String title = article.getTitle();
                String content = article.getContent();
                assertTrue(title.contains(target) || title.contains(target.toUpperCase()) || title.contains(target.toLowerCase())
                        || content.contains(target) || content.contains(target.toUpperCase()) || content.contains(target.toLowerCase()),
                        String.format("Expected title or content '%s' to contain '%s' but it doesn't.", title, target));
            });
        });
    }
}