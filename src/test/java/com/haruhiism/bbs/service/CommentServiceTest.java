package com.haruhiism.bbs.service;

import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.dto.AuthDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentsDTO;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import com.haruhiism.bbs.exception.AuthenticationFailedException;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.repository.CommentRepository;
import com.haruhiism.bbs.service.DataEncoder.DataEncoder;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.comment.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentServiceTest {

    @Autowired
    ArticleService articleService;
    @Autowired
    CommentService commentService;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    DataEncoder dataEncoder;


    // test values
    String testWriter = "testwriter";
    String testPassword = "testpassword";
    String testTitle = "testtitle";
    String testContent = "testcontent";

    String testCommentWriter = "testcommentwriter";
    String testCommentPassword = "testcommentpassword";
    String testCommentContent = "testcommentcontent";

    @Test
    @DisplayName("비로그인 댓글 작성")
    void createCommentWithoutAccountTest() {
        // given
        BoardArticle commentedArticle = new BoardArticle(testWriter, testPassword, testTitle, testContent);
        articleRepository.save(commentedArticle);

        // when
        commentService.createComment(
                BoardCommentDTO.builder()
                        .articleID(commentedArticle.getId())
                        .writer(testCommentWriter)
                        .content(testCommentContent)
                        .password(testCommentPassword).build(),
                AuthDTO.builder().build());

        // then
        BoardCommentsDTO boardCommentsDTO = commentService.readCommentsOfArticle(commentedArticle.getId(), 0, 10);
        assertFalse(boardCommentsDTO.getBoardComments().isEmpty());
        BoardCommentDTO commentDTO = boardCommentsDTO.getBoardComments().get(0);
        assertEquals(testCommentWriter, commentDTO.getWriter());
        assertEquals(testCommentContent, commentDTO.getContent());
        assertEquals(commentedArticle.getId(), commentDTO.getArticleID());
        assertFalse(commentDTO.isWrittenByAccount());
        assertTrue(dataEncoder.compare(testCommentPassword, commentDTO.getPassword()));


        // when
        assertThrows(NoArticleFoundException.class, () -> {
            commentService.createComment(
                    BoardCommentDTO.builder()
                            .articleID(commentedArticle.getId()+1L)
                            .writer(testCommentWriter)
                            .content(testCommentContent)
                            .password(testCommentPassword).build(),
                    AuthDTO.builder().build());
        });
    }

    @Test
    @DisplayName("로그인 댓글 작성")
    void createCommentWithAccountTest(){
        // given
        BoardArticle commentedArticle = new BoardArticle(testWriter, testPassword, testTitle, testContent);
        articleRepository.save(commentedArticle);
        BoardAccount boardAccount = new BoardAccount("userid", "username", "userpassword", "email@domain.com");
        accountRepository.save(boardAccount);

        LoginSessionInfo loginSessionInfo = new LoginSessionInfo(boardAccount);

        // when
        commentService.createComment(
                BoardCommentDTO.builder()
                        .articleID(commentedArticle.getId())
                        .writer(testCommentWriter)
                        .content(testCommentContent)
                        .password(testCommentPassword).build(),
                AuthDTO.builder().
                        loginSessionInfo(loginSessionInfo).build());

        // then
        BoardCommentsDTO boardCommentsDTO = commentService.readCommentsOfArticle(commentedArticle.getId(), 0, 10);
        assertFalse(boardCommentsDTO.getBoardComments().isEmpty());
        BoardCommentDTO commentDTO = boardCommentsDTO.getBoardComments().get(0);
        assertEquals(boardAccount.getUsername(), commentDTO.getWriter());
        assertEquals(testCommentContent, commentDTO.getContent());
        assertEquals(commentedArticle.getId(), commentDTO.getArticleID());
        assertTrue(commentDTO.isWrittenByAccount());


        // when
        assertThrows(NoArticleFoundException.class, () -> {
            commentService.createComment(
                    BoardCommentDTO.builder()
                            .articleID(commentedArticle.getId()+1L)
                            .writer(testCommentWriter)
                            .content(testCommentContent)
                            .password(testCommentPassword).build(),
                    AuthDTO.builder()
                            .loginSessionInfo(loginSessionInfo).build());
        });
    }

    @Test
    @DisplayName("비로그인 댓글 삭제")
    void deleteCommentWithoutAccountTest(){
        // given
        BoardArticle commentedArticle = new BoardArticle(testWriter, testPassword, testTitle, testContent);
        articleRepository.save(commentedArticle);

        BoardComment boardComment = new BoardComment(testCommentWriter, dataEncoder.encode(testCommentPassword), testCommentContent, commentedArticle);
        commentRepository.save(boardComment);

        // then
        assertThrows(AuthenticationFailedException.class, () -> {
            // when
            commentService.deleteComment(boardComment.getId(), AuthDTO.builder().rawPassword("THIS_IS_NOT_YOUR_PASSWORD").build());
        });


        // when
        assertDoesNotThrow(() -> commentService.deleteComment(boardComment.getId(), AuthDTO.builder().rawPassword(testCommentPassword).build()));

        // then
        BoardCommentsDTO boardCommentsDTO = commentService.readCommentsOfArticle(commentedArticle.getId(), 0, 10);
        assertTrue(boardCommentsDTO.getBoardComments().isEmpty());
    }

    @Test
    @DisplayName("로그인 댓글 삭제")
    void deleteCommentWithAccountTest(){
        // given
        BoardArticle commentedArticle = new BoardArticle(testWriter, testPassword, testTitle, testContent);
        articleRepository.save(commentedArticle);
        BoardAccount boardAccount = new BoardAccount("userid", "username", "userpassword", "email@domain.com");
        accountRepository.save(boardAccount);
        BoardComment boardComment = new BoardComment(testCommentWriter, dataEncoder.encode(testCommentPassword), testCommentContent, commentedArticle);
        boardComment.registerCommentWriter(boardAccount);
        commentRepository.save(boardComment);

        LoginSessionInfo loginSessionInfo = new LoginSessionInfo(boardAccount);

        // then
        assertThrows(AuthenticationFailedException.class, () -> {
            // when
            commentService.deleteComment(boardComment.getId(), AuthDTO.builder().loginSessionInfo(null).build());
        });


        // when
        commentService.deleteComment(boardComment.getId(), AuthDTO.builder().loginSessionInfo(loginSessionInfo).build());

        // then
        BoardCommentsDTO boardCommentsDTO = commentService.readCommentsOfArticle(commentedArticle.getId(), 0, 10);
        assertTrue(boardCommentsDTO.getBoardComments().isEmpty());
    }

}