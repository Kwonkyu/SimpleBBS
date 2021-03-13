package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.domain.BoardArticle;
import com.haruhiism.bbs.service.BoardService.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BoardMvcTest {

    @Autowired
    BoardService boardService;

    @Autowired
    MockMvc mockMvc;


    @Test
    void createInvalidBoardArticleTest() throws Exception {
        // given
        MultiValueMap<String, String> params = new HttpHeaders();

        // when
        params.set("writer", "");
        params.set("password", "password");
        params.set("title", "title");
        params.set("content", "content");
        mockMvc.perform(post("/board/write")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());

        // when
        params.set("writer", "writer");
        params.set("password", "");
        mockMvc.perform(post("/board/write")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());

        // when
        params.set("password", "password");
        params.set("title", "");
        mockMvc.perform(post("/board/write")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());

        // when
        params.set("title", "title");
        params.set("content", "");
        mockMvc.perform(post("/board/write")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void readInvalidArticleTest() throws Exception {
        mockMvc.perform(get("/board/read")
                .param("bid", "-1"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/board/read")
                .param("bid", "abcd"))
                .andExpect(status().isUnprocessableEntity());
        // MethodArgumentTypeMismatchException for method parameter type mismatch.
    }

    @Test
    void requestInvalidEditArticleTest() throws Exception {
        // given
        BoardArticle boardArticle = new BoardArticle("writer", "password", "edit_me_title", "edit_me_content");
        boardService.createArticle(boardArticle);

        // when
        mockMvc.perform(post("/board/edit")
                .param("bid", String.valueOf(boardArticle.getBid()))
                .param("password", "THIS_IS_NOT_YOUR_PASSWORD"))
                // then
                .andExpect(status().isUnauthorized());
    }

    @Test
    void submitInvalidEditArticleTest() throws Exception {
        // given
        String originalTitle = "don't_edit_me_title";
        String originalContent = "don't_edit_me_content";
        BoardArticle boardArticle = new BoardArticle("writer", "password", originalTitle, originalContent);
        boardService.createArticle(boardArticle);

        // when
        mockMvc.perform(post("/board/edit/submit")
                .param("bid", "-1")
                .param("writer", "writer")
                .param("password", "password")
                .param("title", "edited_title")
                .param("content", "edited_content"))
                // then
                .andExpect(status().isNotFound());
        // then
        BoardArticle readArticle = boardService.readArticle(boardArticle.getBid());
        assertEquals(readArticle.getTitle(), originalTitle);
        assertEquals(readArticle.getContent(), originalContent);

//        TODO: duplicated test?
//        // when
//        mockMvc.perform(post("/board/edit/submit")
//                .param("bid", String.valueOf(boardArticle.getBid()))
//                .param("writer", "writer")
//                .param("password", "THIS_IS_NOT_YOUR_PASSWORD")
//                .param("title", "edited_title")
//                .param("content", "edited_content"))
//                // then
//                .andExpect(status().isUnauthorized());
//        // then
//        readArticle = boardService.readArticle(boardArticle.getBid());
//        assertEquals(readArticle.getTitle(), originalTitle);
//        assertEquals(readArticle.getContent(), originalContent);
    }

    @Test
    void deleteInvalidArticleTest() throws Exception {
        // given
        BoardArticle boardArticle = new BoardArticle("writer", "password", "title", "content");
        boardService.createArticle(boardArticle);

        // when
        mockMvc.perform(post("/board/remove")
                .param("bid", "-1")
                .param("password", "password"))
                // then
                .andExpect(status().isNotFound());
    }

    @Test
    void requestInvalidDeleteArticleTest() throws Exception {
        // given
        BoardArticle boardArticle = new BoardArticle("writer", "password", "title", "content");
        boardService.createArticle(boardArticle);

        // when
        mockMvc.perform(post("/board/remove")
                .param("bid", String.valueOf(boardArticle.getBid()))
                .param("password", "THIS_IS_NOT_YOUR_PASSWORD"))
                // then
                .andExpect(status().isUnauthorized());
    }
}
