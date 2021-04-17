package com.haruhiism.bbs.mvc;

import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.repository.CommentRepository;
import com.haruhiism.bbs.service.DataEncoder.DataEncoder;
import com.haruhiism.bbs.service.comment.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")

@AutoConfigureMockMvc
@Transactional
public class CommentMvcTest {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    CommentService commentService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    DataEncoder dataEncoder;

    @Test
    @DisplayName("부적절한 댓글(파라미터 누락) 작성")
    void createInvalidCommentTest() throws Exception {
        // given
        BoardArticle boardArticle = new BoardArticle("writer", "password", "title", "content");
        articleRepository.save(boardArticle);

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
        params.set("articleID", String.valueOf(boardArticle.getId()));
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
    @DisplayName("부적절한 댓글 삭제")
    void requestInvalidDeleteCommentTest() throws Exception {
        BoardArticle boardArticle = new BoardArticle("writer", "password", "title", "content");
        articleRepository.save(boardArticle);

        BoardAccount boardAccount = new BoardAccount("userid", "username", "userpassword", "email@domain.com");
        accountRepository.save(boardAccount);

        BoardComment boardCommentWithoutAccount = new BoardComment("commenter", dataEncoder.encode("password"), "comment", boardArticle);
        BoardComment boardCommentWithAccount = new BoardComment("commenter", dataEncoder.encode("password"), "comment", boardArticle);
        boardCommentWithAccount.registerCommentWriter(boardAccount);

        commentRepository.save(boardCommentWithoutAccount);
        commentRepository.save(boardCommentWithAccount);

        // given
        MultiValueMap<String, String> params = new HttpHeaders();
        params.set("id", String.valueOf(boardCommentWithoutAccount.getId()));
        params.set("password", "THIS_IS_NOT_YOUR_PASSWORD");

        MockHttpSession mockHttpSession = new MockHttpSession();

        // when
        mockMvc.perform(post("/comment/remove")
                .params(params))
                // then
                .andExpect(status().isUnauthorized());

        params.set("password", "password");

        // when
        mockMvc.perform(post("/comment/remove")
                .params(params))
                // then
                .andExpect(status().is3xxRedirection());


        // given
        params.set("id", String.valueOf(boardCommentWithAccount.getId()));
        params.remove("password");

        // when
        mockMvc.perform(get("/comment/remove")
                .params(params))
                // then
                .andExpect(status().isUnauthorized());

        // when
        mockMvc.perform(get("/comment/remove")
                .params(params)
                .session(mockHttpSession))
                // then
                .andExpect(status().isUnauthorized());


        mockHttpSession.setAttribute("loginSessionInfo", new LoginSessionInfo(boardAccount));

        // when
        // when
        mockMvc.perform(get("/comment/remove")
                .params(params)
                .session(mockHttpSession))
                // then
                .andExpect(status().is3xxRedirection());
    }
}
