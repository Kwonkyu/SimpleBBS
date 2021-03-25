package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.comment.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BoardMvcTest {

    @Autowired
    ArticleService articleService;
    @Autowired
    CommentService commentService;

    @Autowired
    MockMvc mockMvc;


    @Test
    void createBoardArticleTest() throws Exception {
        // given
        HttpHeaders params = new HttpHeaders();

        // when
        params.set("writer", "writer");
        params.set("password", "password");
        params.set("title", "title");
        params.set("content", "content");
        mockMvc.perform(post("/board/write")
                .params(params))
                // then
                .andExpect(status().is3xxRedirection());
    }

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
                .param("id", "-1"))
                .andExpect(status().isUnprocessableEntity());

        mockMvc.perform(get("/board/read")
                .param("id", "abcd"))
                .andExpect(status().isUnprocessableEntity());
        // MethodArgumentTypeMismatchException for method parameter type mismatch.
    }

    @Test
    void requestInvalidEditArticleTest() throws Exception {
        // given
        BoardArticle boardArticle = new BoardArticle("writer", "password", "edit_me_title", "edit_me_content");
        articleService.createArticle(boardArticle);

        // when
        mockMvc.perform(post("/board/edit")
                .param("articleID", String.valueOf(boardArticle.getArticleID()))
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
        articleService.createArticle(boardArticle);

        // when
        mockMvc.perform(post("/board/edit/submit")
                .param("articleID", "-1")
                .param("writer", "writer")
                .param("password", "password")
                .param("title", "edited_title")
                .param("content", "edited_content"))
                // then
                .andExpect(status().isUnprocessableEntity());
        // then
        BoardArticle readArticle = articleService.readArticle(boardArticle.getArticleID());
        assertEquals(readArticle.getTitle(), originalTitle);
        assertEquals(readArticle.getContent(), originalContent);
    }

    @Test
    void deleteInvalidArticleTest() throws Exception {
        // given
        BoardArticle boardArticle = new BoardArticle("writer", "password", "title", "content");
        articleService.createArticle(boardArticle);

        // when
        mockMvc.perform(post("/board/remove")
                .param("articleID", String.valueOf(boardArticle.getArticleID()+1))
                .param("password", "password"))
                // then
                .andExpect(status().isNotFound());

        // when
        mockMvc.perform(post("/board/remove")
                .param("articleID", "-1")
                .param("password", "password"))
                // then
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void requestInvalidDeleteArticleTest() throws Exception {
        // given
        BoardArticle boardArticle = new BoardArticle("writer", "password", "title", "content");
        articleService.createArticle(boardArticle);

        // when
        mockMvc.perform(post("/board/remove")
                .param("articleID", String.valueOf(boardArticle.getArticleID()))
                .param("password", "THIS_IS_NOT_YOUR_PASSWORD"))
                // then
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createInvalidCommentTest() throws Exception {
        // given
        BoardArticle boardArticle = new BoardArticle("writer", "password", "title", "content");
        articleService.createArticle(boardArticle);

        MultiValueMap<String, String> params = new HttpHeaders();
        params.set("articleID", "");
        params.set("writer", "commenter");
        params.set("password", "comment-password");
        params.set("content", "comment-content");

        // when
        mockMvc.perform(post("/comment/create").params(params))
                // then
                .andExpect(status().isUnprocessableEntity());


        // given
        params.set("articleID", String.valueOf(boardArticle.getArticleID()));
        params.set("writer", "");

        // when
        mockMvc.perform(post("/comment/create").params(params))
                // then
                .andExpect(status().isUnprocessableEntity());


        // given
        params.set("writer", "commenter");
        params.set("password", "");

        // when
        mockMvc.perform(post("/comment/create").params(params))
                // then
                .andExpect(status().isUnprocessableEntity());

        // given
        params.set("password", "comment-password");
        params.set("content", "");

        // when
        mockMvc.perform(post("/comment/create").params(params))
                // then
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void requestInvalidDeleteCommentTest() throws Exception {
        // given
        BoardArticle boardArticle = new BoardArticle("writer", "password", "title", "content");
        articleService.createArticle(boardArticle);
        BoardComment boardComment = new BoardComment("commenter", "password", "comment", boardArticle.getArticleID());
        commentService.createComment(boardComment);

        MultiValueMap<String, String> params = new HttpHeaders();
        params.set("commentID", String.valueOf(boardComment.getCommentID()));
        params.set("password", "THIS_IS_NOT_YOUR_PASSWORD");

        // when
        mockMvc.perform(post("/comment/remove")
                .params(params))
                // then
                .andExpect(status().isUnauthorized());
        assertDoesNotThrow(() -> commentService.readComment(boardComment.getCommentID()));
    }


    @Test
    void deleteInvalidCommentTest() throws Exception {
        // given
        BoardArticle boardArticle = new BoardArticle("writer", "password", "title", "content");
        articleService.createArticle(boardArticle);
        BoardComment boardComment = new BoardComment("commenter", "password", "comment", boardArticle.getArticleID());
        commentService.createComment(boardComment);

        MultiValueMap<String, String> params = new HttpHeaders();
        params.set("commentID", "");
        params.set("password", "password");

        // when
        mockMvc.perform(post("/comment/remove")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());
        assertDoesNotThrow(() -> commentService.readComment(boardComment.getCommentID()));


        params.set("commentID", "0");
        params.set("password", "password");

        // when
        mockMvc.perform(post("/comment/remove")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());
        assertDoesNotThrow(() -> commentService.readComment(boardComment.getCommentID()));


        params.set("commentID", "-1");
        params.set("password", "password");

        // when
        mockMvc.perform(post("/comment/remove")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());
        assertDoesNotThrow(() -> commentService.readComment(boardComment.getCommentID()));
    }

    @Test
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
