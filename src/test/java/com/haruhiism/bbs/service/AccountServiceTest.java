package com.haruhiism.bbs.service;

import com.haruhiism.bbs.domain.UpdatableInformation;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.dto.AuthDTO;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.exception.account.NoAccountFoundException;
import com.haruhiism.bbs.exception.auth.AuthenticationFailedException;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.service.account.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
        // when
        accountService.registerAccount(boardAccountDTO);
        // then
        assertFalse(accountRepository.findByUserIdAndAvailableTrue(testUserId).isEmpty());


        // when
        assertThrows(NoAccountFoundException.class, () -> {
            // then
            accountService.loginAccount(
                    BoardAccountDTO.builder().userId(normalUserId).build(),
                    AuthDTO.builder().rawPassword(normalPassword).build());
        });


        // when
        assertThrows(AuthenticationFailedException.class, () -> {
            // then
            accountService.loginAccount(
                    BoardAccountDTO.builder().userId(testUserId).build(),
                    AuthDTO.builder().rawPassword(normalPassword).build());
        });


        // when
        assertDoesNotThrow(() -> {
            // then
            accountService.loginAccount(
                    BoardAccountDTO.builder().userId(testUserId).build(),
                    AuthDTO.builder().rawPassword(testPassword).build());
        });
    }

    @Test
    @DisplayName("회원탈퇴 시도")
    void withdrawAccountTest() {
        // given
        BoardAccountDTO boardAccountDTO = new BoardAccountDTO(testUserId, testUsername, testPassword, testEmail);
        accountService.registerAccount(boardAccountDTO);

        // when
        accountService.withdrawAccount(
                BoardAccountDTO.builder().userId(testUserId).build(),
                AuthDTO.builder().rawPassword(testPassword).build());

        assertThrows(NoAccountFoundException.class, () -> {
            // then
            accountService.loginAccount(
                    BoardAccountDTO.builder().userId(testUserId).build(),
                    AuthDTO.builder().rawPassword(testPassword).build());
        });

        //then
        assertTrue(accountRepository.findByUserIdAndAvailableTrue(testUserId).isEmpty());
    }

    @Test
    @DisplayName("로그인 시도")
    void loginAccountTest() {
        // given
        BoardAccountDTO boardAccountDTO = new BoardAccountDTO(testUserId, testUsername, testPassword, testEmail);
        accountService.registerAccount(boardAccountDTO);

        // when
        assertThrows(AuthenticationFailedException.class, () -> {
            // then
            accountService.loginAccount(
                    BoardAccountDTO.builder().userId(testUserId).build(),
                    AuthDTO.builder().rawPassword(normalPassword).build());
        });

        // when
        assertThrows(NoAccountFoundException.class, () -> {
            // then
            accountService.loginAccount(
                    BoardAccountDTO.builder().userId(normalUserId).build(),
                    AuthDTO.builder().rawPassword(testPassword).build());
        });

        // when
        LoginSessionInfo loginSessionInfo = accountService.loginAccount(
                BoardAccountDTO.builder().userId(testUserId).build(),
                AuthDTO.builder().rawPassword(testPassword).build());

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
        accountService.registerAccount(boardAccountDTO);

        // when
        accountService.updateAccount(
                BoardAccountDTO.builder().userId(testUserId).build(),
                AuthDTO.builder().rawPassword(testPassword).build(),
                UpdatableInformation.username,
                "updatedusername");

        // then
        Optional<BoardAccount> updateResult = accountRepository.findByUserIdAndAvailableTrue(testUserId);
        assertFalse(updateResult.isEmpty());
        assertEquals("updatedusername", updateResult.get().getUsername());
    }

    @Test
    @DisplayName("계정 이메일 변경")
    void updateEmailTest() {
        // given
        BoardAccountDTO boardAccountDTO = new BoardAccountDTO(testUserId, testUsername, testPassword, testEmail);
        accountService.registerAccount(boardAccountDTO);

        // when
        accountService.updateAccount(
                BoardAccountDTO.builder().userId(testUserId).build(),
                AuthDTO.builder().rawPassword(testPassword).build(),
                UpdatableInformation.email,
                "updateduseremail@domain.com");

        // then
        Optional<BoardAccount> updateResult = accountRepository.findByUserIdAndAvailableTrue(testUserId);
        assertFalse(updateResult.isEmpty());
        assertEquals("updateduseremail@domain.com", updateResult.get().getEmail());
    }

    @Test
    @DisplayName("계정 비밀번호 변경")
    void updatePasswordTest() {
        // given
        BoardAccountDTO boardAccountDTO = new BoardAccountDTO(testUserId, testUsername, testPassword, testEmail);
        accountService.registerAccount(boardAccountDTO);

        // when
        accountService.updateAccount(
                BoardAccountDTO.builder().userId(testUserId).build(),
                AuthDTO.builder().rawPassword(testPassword).build(),
                UpdatableInformation.password,
                "updatedpassword");

        // then
        assertThrows(AuthenticationFailedException.class, () -> accountService.loginAccount(
                BoardAccountDTO.builder().userId(testUserId).build(),
                AuthDTO.builder().rawPassword(testPassword).build()));

        assertDoesNotThrow(() -> accountService.loginAccount(
                BoardAccountDTO.builder().userId(testUserId).build(),
                AuthDTO.builder().rawPassword("updatedpassword").build()));
    }
}