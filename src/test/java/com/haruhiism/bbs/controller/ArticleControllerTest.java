package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.domain.SearchMode;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.exception.UpdateDeletedArticleException;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.service.DataEncoder.DataEncoder;
import com.haruhiism.bbs.service.article.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
class ArticleControllerTest {

    @Autowired
    ArticleService articleService;
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    DataEncoder dataEncoder;


    @Test
    void createAndReadBoardArticleTest() {
        // given
        String content = "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit";
        BoardArticleDTO boardArticle = new BoardArticleDTO("writer01", "p@ssw0rd01", "title01", content);

        // when
        articleService.createArticle(boardArticle, null);
        BoardArticlesDTO articlesDTO = articleService.searchAllByPages(SearchMode.WRITER, "writer01", 0, 10);
        BoardArticleDTO readArticle = articlesDTO.getBoardArticles().get(0);

        // then
        assertEquals(boardArticle.getTitle(), readArticle.getTitle());
        assertEquals(boardArticle.getWriter(), readArticle.getWriter());
        assertEquals(boardArticle.getContent(), readArticle.getContent());
        assertTrue(dataEncoder.compare(boardArticle.getPassword(), readArticle.getPassword()));
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
        articleRepository.save(boardArticle);

        articleService.updateArticle(new BoardArticleDTO(
                boardArticle.getId(),
                "writer",
                "password",
                "edited_title",
                "edited_content"));

        // when
        BoardArticleDTO readArticle = articleService.readArticle(boardArticle.getId());

        // then
        assertEquals("edited_title", readArticle.getTitle());
        assertEquals("edited_content", readArticle.getContent());

        // when
        articleRepository.delete(boardArticle);

        // then
        assertThrows(UpdateDeletedArticleException.class, () -> articleService.updateArticle(
                new BoardArticleDTO(
                        boardArticle.getId(),
                        "writer",
                        "password",
                        "deleted_title",
                        "deleted_content")));
    }

    @Test
    void createAndDeleteArticleTest() {
        // given
        BoardArticle boardArticle = new BoardArticle("writer", "password", "delete_me_title", "delete_me_content");
        articleRepository.save(boardArticle);

        // when
        articleService.deleteArticle(boardArticle.getId());

        // then
        assertThrows(NoArticleFoundException.class, () -> articleService.readArticle(boardArticle.getId()));
    }

    @Test
    void searchArticleTest() {
        // given
        BoardArticleDTO[] boardArticleDTOS = new BoardArticleDTO[]{
                new BoardArticleDTO("Jason", "password", "How was your today", "I was fine."),
                new BoardArticleDTO("Alva", "password", "Zullie where are you?", "Oh Zullie, oh..."),
                new BoardArticleDTO("Zullie", "password", "Alva where are you?", "Oh Alva, oh..."),
                new BoardArticleDTO("Writer", "password", "Hello World", "Cruel World"),
                new BoardArticleDTO("Writer", "password", "Cruel World", "Hello World")
        };

        for (BoardArticleDTO boardArticleDTO : boardArticleDTOS) {
            articleService.createArticle(boardArticleDTO, null);
        }

        Map<String, Integer> writerTestValue = new HashMap<>();
        writerTestValue.put("Writer", 2);
        writerTestValue.put("Alva", 1);
        writerTestValue.put("Zullie", 1);
        writerTestValue.put("Jason", 1);
        writerTestValue.put("e", 3);
        writerTestValue.put("a", 2);

        writerTestValue.forEach((target, count) -> {
            // when
            BoardArticlesDTO articles = articleService.searchAllByPages(SearchMode.WRITER, target, 0, 10);

            // then
            assertThat(articles.getBoardArticles().size()).isEqualTo(count);
            articles.getBoardArticles().forEach(boardArticle -> {
                String writer = boardArticle.getWriter();
                assertTrue(writer.contains(target) || writer.contains(target.toUpperCase()) || writer.contains(target.toLowerCase()),
                        String.format("Expected writer '%s' to contain '%s' but it doesn't.", writer, target));
            });
        });


        Map<String, Integer> titleTestValue = new HashMap<>();
        titleTestValue.put("Zullie", 1);
        titleTestValue.put("Alva", 1);
        titleTestValue.put("where are you?", 2);
        titleTestValue.put("day", 1);
        titleTestValue.put("World", 2);
        titleTestValue.put("h", 4);

        titleTestValue.forEach((target, count) -> {
            // when
            BoardArticlesDTO articles = articleService.searchAllByPages(SearchMode.TITLE, target, 0, 10);

            // then
            assertThat(articles.getBoardArticles().size()).isEqualTo(count);
            articles.getBoardArticles().forEach(boardArticle -> {
                String title = boardArticle.getTitle();
                assertTrue(title.contains(target) || title.contains(target.toUpperCase()) || title.contains(target.toLowerCase()),
                        String.format("Expected title '%s' to contain '%s' but it doesn't.", title, target));
            });
        });


        // given
        Map<String, Integer> contentTestValue = new HashMap<>();
        contentTestValue.put("oh", 2);
        contentTestValue.put("World", 2);
        contentTestValue.put("fine", 1);
        contentTestValue.put("e", 4);
        contentTestValue.put("o", 4);
        contentTestValue.put("ll", 2);

        contentTestValue.forEach((target, count) -> {
            // when
            BoardArticlesDTO articles = articleService.searchAllByPages(SearchMode.CONTENT, target, 0, 10);

            // then
            assertThat(articles.getBoardArticles().size()).isEqualTo(count);
            articles.getBoardArticles().forEach(boardArticle -> {
                String content = boardArticle.getContent();
                assertTrue(content.contains(target) || content.contains(target.toUpperCase()) || content.contains(target.toLowerCase()),
                        String.format("Expected content '%s' to contain '%s' but it doesn't.", content, target));
            });
        });


        // given
        Map<String, Integer> titleOrContentTestValue = new HashMap<>();
        titleOrContentTestValue.put("oh", 2);
        titleOrContentTestValue.put("you", 3);
        titleOrContentTestValue.put("Hello", 2);
        titleOrContentTestValue.put("Cruel", 2);

        titleOrContentTestValue.forEach((target, count) -> {
            // when
            BoardArticlesDTO articles = articleService.searchAllByPages(SearchMode.TITLE_CONTENT, target, 0, 10);

            // then
            assertThat(articles.getBoardArticles().size()).isEqualTo(count);
            articles.getBoardArticles().forEach(article -> {
                String title = article.getTitle();
                String content = article.getContent();
                assertTrue(title.contains(target) || title.contains(target.toUpperCase()) || title.contains(target.toLowerCase())
                        || content.contains(target) || content.contains(target.toUpperCase()) || content.contains(target.toLowerCase()),
                        String.format("Expected title or content '%s' to contain '%s' but it doesn't.", title, target));
            });
        });
    }
}