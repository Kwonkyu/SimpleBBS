package com.haruhiism.bbs.mvc;


import com.haruhiism.bbs.domain.UpdatableInformation;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.dto.AuthDTO;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.service.account.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    // test values
    String testUserId = "testuserid";
    String testUsername = "testusername";
    String testPassword = "testuserpassword";
    String testEmail = "testemail@domain.com";

    String normalUserId = "normaluserid";
    String normalPassword = "normalpassword";


    @Test
    @DisplayName("허가되지 않은 접근")
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
    @DisplayName("부적절한 회원가입 시도")
    void submitInvalidRegisterTest() throws Exception {
        // given
        HttpHeaders params = new HttpHeaders();

        // when
        params.set("userid", "");
        params.set("username", testUsername);
        params.set("password", testPassword);
        params.set("email", testEmail);

        mockMvc.perform(post("/account/register")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());

        // when
        params.set("userid", testUserId);
        params.set("username", "");

        mockMvc.perform(post("/account/register")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());

        // when
        params.set("username", testUsername);
        params.set("password", "");

        mockMvc.perform(post("/account/register")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());

        // when
        params.set("password", testPassword);
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
    @DisplayName("부적절한 회원탈퇴 시도")
    void submitInvalidWithdrawTest() throws Exception {
        // given
        accountService.registerAccount(
                new BoardAccountDTO(testUserId, testUsername, testPassword, testEmail)
        );

        LoginSessionInfo loginSessionInfo = accountService.loginAccount(
                BoardAccountDTO.builder().userId(testUserId).build(),
                AuthDTO.builder().rawPassword(testPassword).build());
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loginSessionInfo", loginSessionInfo);

        HttpHeaders params = new HttpHeaders();
        params.set("password", normalPassword);

        // when
        mockMvc.perform(post("/account/withdraw")
                .session(session)
                .params(params))
                // then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("부적절한 로그인 시도")
    void submitInvalidLoginTest() throws Exception {
        // given
        accountService.registerAccount(
                new BoardAccountDTO(testUserId, testUsername, testPassword, testEmail)
        );

        HttpHeaders params = new HttpHeaders();
        params.set("userid", "");
        params.set("password", testPassword);

        // when
        mockMvc.perform(post("/account/login")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());


        // given
        params.set("userid", testUserId);
        params.set("password", "");

        // when
        mockMvc.perform(post("/account/login")
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());


        // given
        params.set("password", normalPassword);

        // when
        mockMvc.perform(post("/account/login")
                .params(params))
                // then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("부적절한 회원정보 갱신")
    void submitInvalidInfoUpdateTest() throws Exception {
        // given
        accountService.registerAccount(
                new BoardAccountDTO(testUserId, testUsername, testPassword, testEmail)
        );

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loginSessionInfo", accountService.loginAccount(
                BoardAccountDTO.builder().userId(testUserId).build(),
                AuthDTO.builder().rawPassword(testPassword).build()));

        HttpHeaders params = new HttpHeaders();
        params.set("mode", "");
        params.set("auth", testPassword);
        params.set("previous", testUsername);
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
        params.set("auth", testPassword);
        params.set("updated", ""); // currently previous field value has nothing to do with authorization.

        // when
        mockMvc.perform(post("/account/manage/change")
                .session(session)
                .params(params))
                // then
                .andExpect(status().isUnprocessableEntity());


        // given
        params.set("updated", "updatedusername");
        params.set("auth", normalPassword);

        // when
        mockMvc.perform(post("/account/manage/change")
                .session(session)
                .params(params))
                // then
                .andExpect(status().isUnauthorized());
    }
}
