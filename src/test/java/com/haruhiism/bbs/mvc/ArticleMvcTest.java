package com.haruhiism.bbs.mvc;

import com.haruhiism.bbs.controller.ArticleController;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.service.DataEncoder.DataEncoder;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.authentication.LoginSessionInfo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ArticleMvcTest {

    @Autowired
    ArticleService articleService;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    DataEncoder dataEncoder;


    @Test
    @DisplayName("게시글 작성")
    void createArticleTest() throws Exception {
        // given
        BoardAccount boardAccount = new BoardAccount("userid", "username", dataEncoder.encode("password"), "email@domain.com");
        accountRepository.save(boardAccount);

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("loginAuthInfo", new LoginSessionInfo(boardAccount));

        HttpHeaders params = new HttpHeaders();
        params.set("writer", "writer");
        params.set("password", "password");
        params.set("title", "title");
        params.set("content", "content");

        // when
        mockMvc.perform(post("/board/write")
                .params(params))
                // then
                .andExpect(status().is3xxRedirection());

        // when
        mockMvc.perform(post("/board/write")
                .params(params)
                .session(mockHttpSession))
                // then
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("부적절한 게시글 작성")
    void createInvalidArticleTest() throws Exception {
        // given
        BoardAccount boardAccount = new BoardAccount("userid", "username", dataEncoder.encode("password"), "email@domain.com");
        accountRepository.save(boardAccount);

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("loginAuthInfo", new LoginSessionInfo(boardAccount));

        HttpHeaders params = new HttpHeaders();

        // when
        params.set("writer", "");
        params.set("password", "password");
        params.set("title", "title");
        params.set("content", "content");
        mockMvc.perform(post("/board/write")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());

        mockMvc.perform(post("/board/write")
                .params(params)
                .session(mockHttpSession))
                // then
                .andExpect(status().isUnprocessableEntity());


        // when
        params.set("writer", "writer");
        params.set("password", "");
        mockMvc.perform(post("/board/write")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());

        mockMvc.perform(post("/board/write")
                .params(params)
                .session(mockHttpSession))
                // then
                .andExpect(status().isUnprocessableEntity());


        // when
        params.set("password", "password");
        params.set("title", "");
        mockMvc.perform(post("/board/write")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());

        mockMvc.perform(post("/board/write")
                .params(params)
                .session(mockHttpSession))
                // then
                .andExpect(status().isUnprocessableEntity());


        // when
        params.set("title", "title");
        params.set("content", "");
        mockMvc.perform(post("/board/write")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());

        mockMvc.perform(post("/board/write")
                .params(params)
                .session(mockHttpSession))
                // then
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("부적절한 게시물 조회")
    void readInvalidArticleTest() throws Exception {
        mockMvc.perform(get("/board/read")
                .param("id", "-1"))
                .andExpect(status().isUnprocessableEntity());

        mockMvc.perform(get("/board/read")
                .param("id", "abcd"))
                .andExpect(status().isUnprocessableEntity());
        // TODO: MethodArgumentTypeMismatchException for method parameter type mismatch? or BindException? What's difference?
    }

    @Test
    @DisplayName("부적절한 게시물 수정")
    void editInvalidArticleTest() throws Exception {
        // given
        BoardAccount boardAccount = new BoardAccount("userid", "username", dataEncoder.encode("password"), "email@domain.com");
        accountRepository.save(boardAccount);
        BoardArticle boardArticle1 = new BoardArticle("writer", dataEncoder.encode("password"), "edit_me_title", "edit_me_content");
        BoardArticle boardArticle2 = new BoardArticle("writer", dataEncoder.encode("password"), "edit_me_title", "edit_me_content");
        boardArticle2.registerAccountInfo(boardAccount);

        MockHttpSession mockHttpSession = new MockHttpSession();

        articleRepository.save(boardArticle1);
        articleRepository.save(boardArticle2);

        // when
        mockMvc.perform(post("/board/edit")
                .param("id", String.valueOf(boardArticle1.getId()))
                .param("password", "THIS_IS_NOT_YOUR_PASSWORD"))
                // then
                .andExpect(status().isUnauthorized());

        // when
        mockMvc.perform(get("/board/edit")
                .param("id", String.valueOf(boardArticle2.getId()))
                .session(mockHttpSession))
                // then
                .andExpect(status().isUnauthorized());

        // when
        mockMvc.perform(post("/board/edit/submit")
                .param("articleID", String.valueOf(boardArticle1.getId()))
                .param("writer", "writer")
                .param("password", "THIS_IS_NOT_YOUR_PASSWORD")
                .param("title", "edited_title")
                .param("content", "edited_content"))
                // then
                .andExpect(status().isUnauthorized());

        // when
        mockMvc.perform(post("/board/edit/submit")
                .param("articleID", String.valueOf(boardArticle2.getId()))
                .param("writer", "writer")
                .param("password", "password")
                .param("title", "edited_title")
                .param("content", "edited_content")
                .session(mockHttpSession))
                // then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("부적절한 게시물 삭제")
    void deleteInvalidArticleTest() throws Exception {
        // given
        BoardAccount boardAccount = new BoardAccount("userid", "username", dataEncoder.encode("password"), "email@domain.com");
        accountRepository.save(boardAccount);
        BoardArticle boardArticle1 = new BoardArticle("writer", dataEncoder.encode("password"), "edit_me_title", "edit_me_content");
        BoardArticle boardArticle2 = new BoardArticle("writer", dataEncoder.encode("password"), "edit_me_title", "edit_me_content");
        boardArticle2.registerAccountInfo(boardAccount);

        MockHttpSession mockHttpSession = new MockHttpSession();

        articleRepository.save(boardArticle1);
        articleRepository.save(boardArticle2);

        // when
        mockMvc.perform(post("/board/remove")
                .param("id", String.valueOf(boardArticle1.getId()))
                .param("password", "THIS_IS_NOT_YOUR_PASSWORD"))
                // then
                .andExpect(status().isUnauthorized());


        // when
        mockMvc.perform(get("/board/remove")
                .param("id", String.valueOf(boardArticle2.getId()))
                .session(mockHttpSession))
                // then
                .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("게시글 검색")
    void requestSearchTest() throws Exception {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.set("mode", "WRITER");
        headers.set("keyword", "normal_keyword");

        // when
        mockMvc.perform(get("/board/search")
                .params(headers))
                // then
                .andExpect(status().isOk());


        // given
        headers.set("mode", "TITLE");

        // when
        mockMvc.perform(get("/board/search")
                .params(headers))
                // then
                .andExpect(status().isOk());


        // given
        headers.set("mode", "CONTENT");

        // when
        mockMvc.perform(get("/board/search")
                .params(headers))
                // then
                .andExpect(status().isOk());


        // given
        headers.set("mode", "TITLE_CONTENT");

        // when
        mockMvc.perform(get("/board/search")
                .params(headers))
                // then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("부적절한 게시글 검색")
    void requestInvalidSearchTest() throws Exception {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.set("mode", "UNAVAILABLE_MODE");
        headers.set("keyword", "normal_keyword");

        // when
        mockMvc.perform(get("/board/search")
                .params(headers))
                // then
                .andExpect(status().isUnprocessableEntity());


        // given
        headers.set("mode", "");

        // when
        mockMvc.perform(get("/board/search")
                .params(headers))
                // then
                .andExpect(status().isUnprocessableEntity());


        // given
        headers.set("mode", "CONTENT");
        headers.set("keyword", "");

        // when
        mockMvc.perform(get("/board/search")
                .params(headers))
                // then
                .andExpect(status().isUnprocessableEntity());
    }
}
