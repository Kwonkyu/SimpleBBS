package com.haruhiism.bbs.service;

import com.haruhiism.bbs.domain.SearchMode;
import com.haruhiism.bbs.domain.dto.BoardArticleAuthDTO;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.exception.AuthenticationFailedException;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.exception.UpdateDeletedArticleException;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.service.DataEncoder.DataEncoder;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.authentication.LoginSessionInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ArticleServiceTest {

    @Autowired
    ArticleService articleService;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    DataEncoder dataEncoder;


    @Test
    @DisplayName("비로그인 게시글 작성 및 조회")
    void createArticleWithoutAccountTest() {
        // given
        BoardArticleDTO boardArticleDTO = BoardArticleDTO.builder()
                .writer("writer01")
                .password("p@ssw0rd01")
                .title("title01")
                .content("content").build();

        // when
        articleService.createArticle(boardArticleDTO, null);
        BoardArticlesDTO articlesDTO1 = articleService.searchAllByPages(SearchMode.WRITER, "writer01", 0, 10);
        BoardArticleDTO createdArticle1 = articlesDTO1.getBoardArticles().get(0);

        // then
        assertEquals(boardArticleDTO.getTitle(), createdArticle1.getTitle());
        assertEquals(boardArticleDTO.getWriter(), createdArticle1.getWriter());
        assertEquals(boardArticleDTO.getContent(), createdArticle1.getContent());
        assertTrue(dataEncoder.compare(boardArticleDTO.getPassword(), createdArticle1.getPassword()));
    }

    @Test
    @DisplayName("로그인 게시글 작성 및 조회")
    void createArticleWithAccountTest(){
        // given
        BoardAccount account = new BoardAccount("userid", "username", "userpassword", "email@domain.com");
        accountRepository.save(account);

        BoardArticleDTO boardArticleDTO = BoardArticleDTO.builder()
                .writer("writer02")
                .password("PASSWORD")
                .title("title02")
                .content("content02")
                .build();

        // when
        articleService.createArticle(
                boardArticleDTO,
                BoardArticleAuthDTO.builder().loginSessionInfo(new LoginSessionInfo(account)).build());
        BoardArticlesDTO articlesDTO2 = articleService.searchAllByPages(SearchMode.WRITER, "writer02", 0, 10);
        BoardArticleDTO createdArticle = articlesDTO2.getBoardArticles().get(0);

        // then
        assertEquals(boardArticleDTO.getTitle(), createdArticle.getTitle());
        assertEquals(boardArticleDTO.getWriter(), createdArticle.getWriter());
        assertEquals(boardArticleDTO.getContent(), createdArticle.getContent());
        assertTrue(dataEncoder.compare(boardArticleDTO.getPassword(), createdArticle.getPassword()));
        assertTrue(createdArticle.isWrittenByAccount());
    }


    @Test
    @DisplayName("부적절한 게시글 조회")
    void readInvalidArticleTest() {
        assertThrows(NoArticleFoundException.class, () -> articleService.readArticle(-1L));
        assertThrows(NoArticleFoundException.class, () -> articleService.readArticle(0L));
    }

    @Test
    @DisplayName("비로그인 게시글 작성 후 수정")
    void editArticleWithoutAccountTest() {
        // given
        BoardArticle boardArticle = new BoardArticle("writer", "password", "edit_me_title", "edit_me_content");
        articleRepository.save(boardArticle);

        // when
        articleService.updateArticle(
                BoardArticleDTO.builder()
                        .id(boardArticle.getId())
                        .title("edited_title")
                        .content("edited_content").build(),
                BoardArticleAuthDTO.builder().rawPassword("password").build());

        BoardArticleDTO createdArticle = articleService.readArticle(boardArticle.getId());

        // then
        assertEquals("edited_title", createdArticle.getTitle());
        assertEquals("edited_content", createdArticle.getContent());


        // when
        articleService.updateArticle(
                BoardArticleDTO.builder()
                        .id(boardArticle.getId())
                        .title("edited_again_title")
                        .content("edited_again_content").build(),
                BoardArticleAuthDTO.builder().rawPassword("NOT_THIS_PASSWORD").build());

        createdArticle = articleService.readArticle(boardArticle.getId());

        // then
        assertNotEquals("edited_again_title", createdArticle.getTitle());
        assertNotEquals("edited_again_content", createdArticle.getContent());
    }

    @Test
    @DisplayName("로그인 게시글 작성 후 수정")
    void editArticleWithAccountTest(){
        // given
        BoardArticle boardArticle = new BoardArticle("writer", "password", "edit_me_title", "edit_me_content");
        BoardAccount boardAccount = new BoardAccount("userid", "username", "userpassword", "email@domain.com");

        accountRepository.save(boardAccount);
        boardArticle.registerAccountInfo(boardAccount);
        articleRepository.save(boardArticle);

        // when
        articleService.updateArticle(
                BoardArticleDTO.builder()
                        .id(boardArticle.getId())
                        .title("edited_title")
                        .content("edited_content").build(),
                BoardArticleAuthDTO.builder().loginSessionInfo(new LoginSessionInfo(boardAccount)).build());

        BoardArticleDTO createdArticle = articleService.readArticle(boardArticle.getId());

        // then
        assertEquals("edited_title", createdArticle.getTitle());
        assertEquals("edited_content", createdArticle.getContent());


        // when
        articleService.updateArticle(
                BoardArticleDTO.builder()
                        .id(boardArticle.getId())
                        .title("edited_again_title")
                        .content("edited_again_content").build(),
                BoardArticleAuthDTO.builder().loginSessionInfo(null).build());

        createdArticle = articleService.readArticle(boardArticle.getId());

        // then
        assertNotEquals("edited_again_title", createdArticle.getTitle());
        assertNotEquals("edited_again_content", createdArticle.getContent());
    }

    @Test
    @DisplayName("비로그인 게시글 작성 및 삭제")
    void deleteArticleWithoutAccountTest() {
        // given
        BoardArticle boardArticle1 = new BoardArticle("writer", "password", "delete_me_title", "delete_me_content");
        BoardArticle boardArticle2 = new BoardArticle("writer", "password", "delete_me_title", "delete_me_content");
        articleRepository.save(boardArticle1);

        // when
        assertDoesNotThrow(() ->
                articleService.deleteArticle(
                        boardArticle1.getId(),
                        BoardArticleAuthDTO.builder().rawPassword("password").build()));

        // then
        assertThrows(NoArticleFoundException.class, () -> articleService.readArticle(boardArticle1.getId()));
        assertThrows(UpdateDeletedArticleException.class, () -> articleService.updateArticle(
                BoardArticleDTO.builder().id(boardArticle1.getId()).build(), null));


        // when
        assertThrows(AuthenticationFailedException.class, () ->
                articleService.deleteArticle(
                        boardArticle2.getId(),
                        BoardArticleAuthDTO.builder().rawPassword("NOT_THIS_PASSWORD").build()));

        // then
        assertDoesNotThrow(() -> articleService.readArticle(boardArticle2.getId()));
    }

    @Test
    @DisplayName("로그인 게시글 작성 및 삭제")
    void deleteArticleWithAccountTest(){
        // given
        // TODO: 공통부분 함수로 추출?
        // Transactional won't be applied to test life cycle methods.
        BoardAccount account = new BoardAccount("userid", "username", "userpassword", "email@domain.com");
        BoardArticle boardArticle1 = new BoardArticle("writer", "password", "delete_me_title", "delete_me_content");
        BoardArticle boardArticle2 = new BoardArticle("writer", "password", "delete_me_title", "delete_me_content");

        accountRepository.save(account);
        boardArticle1.registerAccountInfo(account);
        boardArticle2.registerAccountInfo(account);
        articleRepository.save(boardArticle1);

        // when
        assertDoesNotThrow(() ->
                articleService.deleteArticle(
                        boardArticle1.getId(),
                        BoardArticleAuthDTO.builder().loginSessionInfo(new LoginSessionInfo(account)).build()));

        // then
        assertThrows(NoArticleFoundException.class, () -> articleService.readArticle(boardArticle1.getId()));
        assertThrows(UpdateDeletedArticleException.class, () -> articleService.updateArticle(
                BoardArticleDTO.builder().id(boardArticle1.getId()).build(), null));


        // when
        assertThrows(AuthenticationFailedException.class, () ->
                articleService.deleteArticle(
                        boardArticle2.getId(),
                        BoardArticleAuthDTO.builder().loginSessionInfo(null).build()));

        // then
        assertDoesNotThrow(() -> articleService.readArticle(boardArticle2.getId()));
    }

    @Test
    @DisplayName("게시글 검색")
    void searchArticleTest() {
        // given
        BoardArticleDTO[] boardArticleDTOS = new BoardArticleDTO[]{
                BoardArticleDTO.builder().writer("Jason").password("password").title("How was your today").content("I was fine.").build(),
                BoardArticleDTO.builder().writer("Alva").password("password").title("Zullie where are you?").content("Oh Zullie, oh...").build(),
                BoardArticleDTO.builder().writer("Zullie").password("password").title("Alva where are you?").content("Oh Alva, oh...").build(),
                BoardArticleDTO.builder().writer("Writer").password("password").title("Hello World").content("Cruel World").build(),
                BoardArticleDTO.builder().writer("Writer").password("password").title("Cruel World").content("Hello World").build()
        };

        for (BoardArticleDTO boardArticleDTO : boardArticleDTOS) {
            articleService.createArticle(boardArticleDTO, BoardArticleAuthDTO.builder().build());
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