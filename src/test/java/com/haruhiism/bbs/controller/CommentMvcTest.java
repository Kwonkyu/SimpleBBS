package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.repository.CommentRepository;
import com.haruhiism.bbs.service.comment.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CommentMvcTest {

    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    CommentService commentService;

    @Autowired
    MockMvc mockMvc;


    @Test
    void requestInvalidDeleteCommentTest() throws Exception {
        // given
        BoardArticle boardArticle = new BoardArticle("writer", "password", "title", "content");
        articleRepository.save(boardArticle);
        BoardComment boardComment = new BoardComment("commenter", "password", "comment", boardArticle);
        commentRepository.save(boardComment);

        MultiValueMap<String, String> params = new HttpHeaders();
        params.set("commentID", String.valueOf(boardComment.getId()));
        params.set("password", "THIS_IS_NOT_YOUR_PASSWORD");

        // when
        mockMvc.perform(post("/comment/remove")
                .params(params))
                // then
                .andExpect(status().isUnauthorized());
        assertDoesNotThrow(() -> commentService.readComment(boardComment.getId()));
    }


    @Test
    void deleteInvalidCommentTest() throws Exception {
        // given
        BoardArticle boardArticle = new BoardArticle("writer", "password", "title", "content");
        articleRepository.save(boardArticle);
        BoardComment boardComment = new BoardComment("commenter", "password", "comment", boardArticle);
        commentRepository.save(boardComment);

        MultiValueMap<String, String> params = new HttpHeaders();
        params.set("commentID", "");
        params.set("password", "password");

        // when
        mockMvc.perform(post("/comment/remove")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());
        assertDoesNotThrow(() -> commentService.readComment(boardComment.getId()));


        params.set("commentID", "0");
        params.set("password", "password");

        // when
        mockMvc.perform(post("/comment/remove")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());
        assertDoesNotThrow(() -> commentService.readComment(boardComment.getId()));


        params.set("commentID", "-1");
        params.set("password", "password");

        // when
        mockMvc.perform(post("/comment/remove")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());
        assertDoesNotThrow(() -> commentService.readComment(boardComment.getId()));
    }
}
