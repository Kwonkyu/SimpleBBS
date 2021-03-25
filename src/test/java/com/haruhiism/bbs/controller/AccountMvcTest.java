package com.haruhiism.bbs.controller;


import com.haruhiism.bbs.command.account.UpdatableInformation;
import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.service.account.AccountService;
import com.haruhiism.bbs.service.authentication.LoginSessionInfo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AccountMvcTest {

    @Autowired
    AccountService accountService;

    @Autowired
    MockMvc mockMvc;


    @Test
    void unauthorizedAccessTest() throws Exception {
        // given
        MockHttpSession mockHttpSession = new MockHttpSession();

        // when
        mockMvc.perform(get("/account/register")
                .session(mockHttpSession))
                // then
                .andExpect(status().is3xxRedirection());

        // when
        mockMvc.perform(get("/account/login")
                .session(mockHttpSession))
                // then
                .andExpect(status().is3xxRedirection());


        // when
        mockMvc.perform(get("/account/logout"))
                // then
                .andExpect(status().is3xxRedirection());

        // when
        mockMvc.perform(get("/account/withdraw"))
                // then
                .andExpect(status().is3xxRedirection());

        // when
        mockMvc.perform(get("/account/manage"))
                // then
                .andExpect(status().is3xxRedirection());

        // when
        mockMvc.perform(get("/account/manage/change"))
                // then
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void submitInvalidRegisterTest() throws Exception {
        // given
        accountService.registerAccount(
                new BoardAccount("testuserid", "testusername", "testpassword", "testemail@domain.com"),
                AccountLevel.NORMAL);
        HttpHeaders params = new HttpHeaders();

        // when
        params.set("userid", "");
        params.set("username", "testusername");
        params.set("password", "testpassword");
        params.set("email", "testemail@domain.com");

        mockMvc.perform(post("/account/register")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());

        // when
        params.set("userid", "testuserid");
        params.set("username", "");

        mockMvc.perform(post("/account/register")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());

        // when
        params.set("username", "testusername");
        params.set("password", "");

        mockMvc.perform(post("/account/register")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());

        // when
        params.set("password", "testuserpassword");
        params.set("email", "");

        mockMvc.perform(post("/account/register")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());

        // when
        params.set("email", "this_is_not_email...");

        mockMvc.perform(post("/account/register")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void submitInvalidWithdrawTest() throws Exception {
        // given
        accountService.registerAccount(
                new BoardAccount("testuserid", "testusername", "testpassword", "testemail@domain.com"),
                AccountLevel.NORMAL);

        LoginSessionInfo loginSessionInfo = accountService.loginAccount("testuserid", "testpassword");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loginAuthInfo", loginSessionInfo);

        HttpHeaders params = new HttpHeaders();
        params.set("password", "normalpassword");

        // when
        mockMvc.perform(post("/account/withdraw")
                .session(session)
                .params(params))
                // then
                .andExpect(status().isUnauthorized());
    }

    @Test
    void submitInvalidLoginTest() throws Exception {
        // given
        accountService.registerAccount(
                new BoardAccount("testuserid", "testusername", "testpassword", "testemail@domain.com"),
                AccountLevel.NORMAL);

        HttpHeaders params = new HttpHeaders();
        params.set("userid", "");
        params.set("password", "testpassword");

        // when
        mockMvc.perform(post("/account/login")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());


        // given
        params.set("userid", "testuserid");
        params.set("password", "");

        // when
        mockMvc.perform(post("/account/login")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());


        // given
        params.set("password", "normalpassword");

        // when
        mockMvc.perform(post("/account/login")
                .params(params))
                // then
                .andExpect(status().isUnauthorized());
    }

    @Test
    void submitInvalidInfoUpdateTest() throws Exception {
        // given
        accountService.registerAccount(
                new BoardAccount("testuserid", "testusername", "testpassword", "testemail@domain.com"),
                AccountLevel.NORMAL);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loginAuthInfo", accountService.loginAccount("testuserid", "testpassword"));

        HttpHeaders params = new HttpHeaders();
        params.set("mode", "");
        params.set("auth", "testpassword");
        params.set("previous", "testusername");
        params.set("updated", "updatedusername");

        // when
        mockMvc.perform(post("/account/manage/change")
                .session(session)
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());


        // given
        params.set("mode", UpdatableInformation.username.name());
        params.set("auth", "");

        // when
        mockMvc.perform(post("/account/manage/change")
                .session(session)
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());


        // given
        params.set("auth", "testpassword");
        params.set("updated", ""); // currently previous field value has nothing to do with authorization.

        // when
        mockMvc.perform(post("/account/manage/change")
                .session(session)
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());


        // given
        params.set("updated", "updatedusername");
        params.set("auth", "normalpassword");

        // when
        mockMvc.perform(post("/account/manage/change")
                .session(session)
                .params(params))
                // then
                .andExpect(status().isUnauthorized());
    }
}
