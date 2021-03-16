package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.comment.CommentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentControllerTest {

    @Autowired
    ArticleService articleService;
    @Autowired
    CommentService commentService;


    @Test
    void createAndReadArticleCommentTest() {

        BoardArticle commentedArticle = new BoardArticle("testwriter", "testpassword", "testtitle", "testcontent");
        articleService.createArticle(commentedArticle);

        BoardComment comment = new BoardComment("testcommentwriter", "testcommentpassword", "testcommentcontent", commentedArticle.getArticleID());
        commentService.createComment(comment);

        List<BoardComment> comments = commentService.readCommentsOfArticle(commentedArticle.getArticleID());
        assertTrue(comments.contains(comment));

    }

    @Test
    void createInvalidArticleCommentTest(){
        BoardComment comment = new BoardComment("writer", "password", "content", -1L);
        assertThrows(NoArticleFoundException.class, () -> {
            commentService.createComment(comment);
        });
    }
}