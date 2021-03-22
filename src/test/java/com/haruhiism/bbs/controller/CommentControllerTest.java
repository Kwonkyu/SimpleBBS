package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.exception.NoCommentFoundException;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.comment.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
        // given
        BoardArticle commentedArticle = new BoardArticle("testwriter", "testpassword", "testtitle", "testcontent");
        articleService.createArticle(commentedArticle);

        BoardComment comment = new BoardComment("testcommentwriter", "testcommentpassword", "testcommentcontent", commentedArticle.getArticleID());
        commentService.createComment(comment);

        // when
        List<BoardComment> comments = commentService.readCommentsOfArticle(commentedArticle.getArticleID());

        // then
        assertTrue(comments.contains(comment));
    }

    @Test
    void createInvalidArticleCommentTest(){
        // given
        BoardComment comment = new BoardComment("writer", "password", "content", -1L);

        // then
        assertThrows(NoArticleFoundException.class, () -> {
            // when
            commentService.createComment(comment);
        });
    }

    @Test
    void createAndDeleteCommentTest(){
        // given
        BoardArticle commentedArticle = new BoardArticle("testwriter", "testpassword", "testtitle", "testcontent");
        articleService.createArticle(commentedArticle);

        // when
        BoardComment comment = new BoardComment("writer1", "password1", "content1", commentedArticle.getArticleID());
        commentService.createComment(comment);

        // then
        List<BoardComment> comments = commentService.readCommentsOfArticle(commentedArticle.getArticleID());
        assertFalse(comments.isEmpty());

        // when
        commentService.deleteComment(comment);

        // then
        comments = commentService.readCommentsOfArticle(commentedArticle.getArticleID());
        assertTrue(comments.isEmpty());


    }

    @Test
    void deleteInvalidCommentTest(){
        // given
        BoardArticle commentedArticle = new BoardArticle("testwriter", "testpassword", "testtitle", "testcontent");
        articleService.createArticle(commentedArticle);

        // when
        BoardComment comment = new BoardComment("writer2", "password2", "content2", commentedArticle.getArticleID());
        commentService.createComment(comment);

        // then
        assertThrows(NoCommentFoundException.class, () -> commentService.deleteComment(comment.getCommentID()+1));
    }
}