package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.account.UpdatableInformation;
import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.exception.AuthenticationFailedException;
import com.haruhiism.bbs.exception.NoAccountFoundException;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.service.account.AccountService;
import com.haruhiism.bbs.service.authentication.LoginSessionInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AccountControllerTest {

    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;

    private final String sessionAuthAttribute = "loginAuthInfo";


    @Test
    void registerAccountTest() {
        // given
        BoardAccount account = new BoardAccount("testuserid", "testusername", "testuserpassword", "testuseremail@domain.com");
        accountService.registerAccount(account, AccountLevel.NORMAL);

        // when
        assertThrows(NoAccountFoundException.class, () -> {
            // then
            accountService.authenticateAccount("normaluserid", "testuserpassword");
        });

        // when
        assertThrows(AuthenticationFailedException.class, () -> {
            // then
            accountService.authenticateAccount("testuserid", "normaluserpassword");
        });

        // when
        assertDoesNotThrow(() -> {
            // then
            accountService.authenticateAccount("testuserid", "testuserpassword");
        });

        // then
        assertFalse(accountRepository.findByUserID("testuserid").isEmpty());
    }

    @Test
    void withdrawAccountTest() {
        // given
        BoardAccount account = new BoardAccount("testuserid", "testusername", "testuserpassword", "testuseremail@domain.com");
        accountService.registerAccount(account, AccountLevel.NORMAL);

        // when
        accountService.withdrawAccount(account);
        assertThrows(NoAccountFoundException.class, () -> {
            // then
            accountService.authenticateAccount("testuserid", "testuserpassword");
        });

        //then
        assertTrue(accountRepository.findByUserID("testuserid").isEmpty());
    }

    @Test
    void loginAccountTest() {
        // given
        BoardAccount account = new BoardAccount("testuserid", "testusername", "testuserpassword", "testuseremail@domain.com");
        accountService.registerAccount(account, AccountLevel.NORMAL);

        // when
        assertThrows(AuthenticationFailedException.class, () -> {
            // then
            accountService.loginAccount("testuserid", "normaluserpassword");
        });

        // when
        assertThrows(NoAccountFoundException.class, () -> {
            // then
            accountService.loginAccount("normaluserid", "testuserpassword");
        });

        // when
        LoginSessionInfo loginSessionInfo = accountService.loginAccount("testuserid", "testuserpassword");

        // then
        assertEquals("testuserid", loginSessionInfo.getUserID());
        assertEquals("testusername", loginSessionInfo.getUsername());
        assertEquals("testuseremail@domain.com", loginSessionInfo.getEmail());
        assertEquals(AccountLevel.NORMAL, loginSessionInfo.getLevels().get(0).getAccountLevel());
    }


    @Test
    void updateUsernameTest() {
        // given
        BoardAccount account = new BoardAccount("testuserid", "testusername", "testuserpassword", "testuseremail@domain.com");
        accountService.registerAccount(account, AccountLevel.NORMAL);

        // when
        accountService.updateAccount("testuserid", "testuserpassword", UpdatableInformation.username, "updatedusername");

        // then
        assertEquals("updatedusername", accountService.authenticateAccount("testuserid", "testuserpassword").getUsername());
    }

    @Test
    void updateEmailTest() {
        // given
        BoardAccount account = new BoardAccount("testuserid", "testusername", "testuserpassword", "testuseremail@domain.com");
        accountService.registerAccount(account, AccountLevel.NORMAL);

        // when
        accountService.updateAccount("testuserid", "testuserpassword", UpdatableInformation.email, "updateduseremail@domain.com");

        // then
        assertEquals("updateduseremail@domain.com", accountService.authenticateAccount("testuserid", "testuserpassword").getEmail());
    }

    @Test
    void updatePasswordTest() {
        // given
        BoardAccount account = new BoardAccount("testuserid", "testusername", "testuserpassword", "testuseremail@domain.com");
        accountService.registerAccount(account, AccountLevel.NORMAL);

        // when
        accountService.updateAccount("testuserid", "testuserpassword", UpdatableInformation.password, "updatedpassword");

        // then
        assertThrows(AuthenticationFailedException.class, () -> {
            accountService.authenticateAccount("testuserid", "testuserpassword");
        });
        assertDoesNotThrow(() -> {
            accountService.authenticateAccount("testuserid", "updatedpassword");
        });
    }
}