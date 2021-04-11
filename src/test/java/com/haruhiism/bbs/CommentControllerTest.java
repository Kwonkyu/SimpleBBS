package com.haruhiism.bbs;

import com.haruhiism.bbs.domain.dto.BoardCommentDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentsDTO;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.exception.NoCommentFoundException;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.repository.CommentRepository;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.comment.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentControllerTest {

    @Autowired
    ArticleService articleService;
    @Autowired
    CommentService commentService;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    CommentRepository commentRepository;


    // test values
    String testWriter = "testwriter";
    String testPassword = "testpassword";
    String testTitle = "testtitle";
    String testContent = "testcontent";

    String testCommentWriter = "testcommentwriter";
    String testCommentPassword = "testcommentpassword";
    String testCommentContent = "testcommentcontent";

    @Test
    void createAndReadArticleCommentTest() {
        // given
        BoardArticle commentedArticle = new BoardArticle(testWriter, testPassword, testTitle, testContent);
        articleRepository.save(commentedArticle);

        BoardComment comment = new BoardComment(testCommentWriter, testCommentPassword, testCommentContent, commentedArticle);
        commentRepository.save(comment);

        // when
        BoardCommentsDTO commentsDTO = commentService.readCommentsOfArticle(commentedArticle.getId(), 0, 10);
        BoardCommentDTO commentDTO = commentsDTO.getBoardComments().get(0);

        // then
        assertEquals(testCommentWriter, commentDTO.getWriter());
        assertEquals(testCommentContent, commentDTO.getContent());
    }

    @Test
    void createInvalidArticleCommentTest(){
        // given

        // then
        assertThrows(NoArticleFoundException.class, () -> {
            // when
            commentService.createComment(new BoardCommentDTO(-1L, testWriter, testPassword, testContent));
        });
    }

    @Test
    void createAndDeleteCommentTest(){
        // given
        BoardArticle commentedArticle = new BoardArticle(testWriter, testPassword, testTitle, testContent);
        articleRepository.save(commentedArticle);


        // when
        BoardComment comment = new BoardComment(testCommentWriter, testCommentPassword, testCommentContent, commentedArticle);
        commentRepository.save(comment);

        // then
        BoardCommentsDTO comments = commentService.readCommentsOfArticle(commentedArticle.getId(), 0, 10);
        assertFalse(comments.getBoardComments().isEmpty());


        // when
        commentService.deleteComment(comment.getId());

        // then
        comments = commentService.readCommentsOfArticle(commentedArticle.getId(), 0, 10);
        assertTrue(comments.getBoardComments().isEmpty());
    }

    @Test
    void deleteInvalidCommentTest(){
        // given
        BoardArticle commentedArticle = new BoardArticle("testwriter", "testpassword", "testtitle", "testcontent");
        articleRepository.save(commentedArticle);

        // when
        BoardComment comment = new BoardComment("writer2", "password2", "content2", commentedArticle);
        commentRepository.save(comment);

        // then
        assertThrows(NoCommentFoundException.class, () -> commentService.deleteComment(comment.getId()+1));
    }
}