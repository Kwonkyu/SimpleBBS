package com.haruhiism.bbs.service;

import com.haruhiism.bbs.domain.UpdatableInformation;
import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.exception.AuthenticationFailedException;
import com.haruhiism.bbs.exception.NoAccountFoundException;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.service.account.AccountService;
import com.haruhiism.bbs.service.authentication.LoginSessionInfo;
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
class AccountServiceTest {

    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;

    // test values
    String testUserId = "testuserid";
    String testUsername = "testusername";
    String testPassword = "testuserpassword";
    String testEmail = "testemail@domain.com";

    String normalUserId = "normaluserid";
    String normalPassword = "normalpassword";


    @Test
    @DisplayName("회원가입 시도")
    void registerAccountTest() {
        // given
        BoardAccountDTO boardAccountDTO = new BoardAccountDTO(testUserId, testUsername, testPassword, testEmail);
        accountService.registerAccount(boardAccountDTO, AccountLevel.NORMAL);

        // when
        assertThrows(NoAccountFoundException.class, () -> {
            // then
            accountService.authenticateAccount(normalUserId, normalPassword);
        });

        // when
        assertThrows(AuthenticationFailedException.class, () -> {
            // then
            accountService.authenticateAccount(testUserId, normalPassword);
        });

        // when
        assertDoesNotThrow(() -> {
            // then
            accountService.authenticateAccount(testUserId, testPassword);
        });

        // then
        assertFalse(accountRepository.findByUserId(testUserId).isEmpty());
    }

    @Test
    @DisplayName("회원탈퇴 시도")
    void withdrawAccountTest() {
        // given
        BoardAccountDTO boardAccountDTO = new BoardAccountDTO(testUserId, testUsername, testPassword, testEmail);
        accountService.registerAccount(boardAccountDTO, AccountLevel.NORMAL);

        // when
        accountService.withdrawAccount(accountService.authenticateAccount(testUserId, testPassword));
        assertThrows(NoAccountFoundException.class, () -> {
            // then
            accountService.authenticateAccount(testUserId, testPassword);
        });

        //then
        assertTrue(accountRepository.findByUserId(testUserId).isEmpty());
    }

    @Test
    @DisplayName("로그인 시도")
    void loginAccountTest() {
        // given
        BoardAccountDTO boardAccountDTO = new BoardAccountDTO(testUserId, testUsername, testPassword, testEmail);
        accountService.registerAccount(boardAccountDTO, AccountLevel.NORMAL);

        // when
        assertThrows(AuthenticationFailedException.class, () -> {
            // then
            accountService.loginAccount(testUserId, normalPassword);
        });

        // when
        assertThrows(NoAccountFoundException.class, () -> {
            // then
            accountService.loginAccount(normalUserId, testPassword);
        });

        // when
        LoginSessionInfo loginSessionInfo = accountService.loginAccount(testUserId, testPassword);

        // then
        assertEquals(testUserId, loginSessionInfo.getUserID());
        assertEquals(testUsername, loginSessionInfo.getUsername());
        assertEquals(testEmail, loginSessionInfo.getEmail());
    }


    @Test
    @DisplayName("계정 이름 변경")
    void updateUsernameTest() {
        // given
        BoardAccountDTO boardAccountDTO = new BoardAccountDTO(testUserId, testUsername, testPassword, testEmail);
        accountService.registerAccount(boardAccountDTO, AccountLevel.NORMAL);

        // when
        accountService.updateAccount(testUserId, testPassword, UpdatableInformation.username, "updatedusername");

        // then
        assertEquals("updatedusername", accountService.authenticateAccount(testUserId, testPassword).getUsername());
    }

    @Test
    @DisplayName("계정 이메일 변경")
    void updateEmailTest() {
        // given
        BoardAccountDTO boardAccountDTO = new BoardAccountDTO(testUserId, testUsername, testPassword, testEmail);
        accountService.registerAccount(boardAccountDTO, AccountLevel.NORMAL);

        // when
        accountService.updateAccount(testUserId, testPassword, UpdatableInformation.email, "updateduseremail@domain.com");

        // then
        assertEquals("updateduseremail@domain.com", accountService.authenticateAccount(testUserId, testPassword).getEmail());
    }

    @Test
    @DisplayName("계정 비밀번호 변경")
    void updatePasswordTest() {
        // given
        BoardAccountDTO boardAccountDTO = new BoardAccountDTO(testUserId, testUsername, testPassword, testEmail);
        accountService.registerAccount(boardAccountDTO, AccountLevel.NORMAL);

        // when
        accountService.updateAccount(testUserId, testPassword, UpdatableInformation.password, "updatedpassword");

        // then
        assertThrows(AuthenticationFailedException.class, () -> accountService.authenticateAccount(testUserId, testPassword));
        assertDoesNotThrow(() -> {
            accountService.authenticateAccount(testUserId, "updatedpassword");
        });
    }
}