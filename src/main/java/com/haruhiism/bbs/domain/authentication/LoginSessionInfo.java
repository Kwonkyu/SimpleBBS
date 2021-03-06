package com.haruhiism.bbs.domain.authentication;

import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@RequiredArgsConstructor
public class LoginSessionInfo {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Long accountID;
    private final String userID;
    private final String username;
    private final String email;
    private final String registeredDate;

    public LoginSessionInfo(BoardAccount account) {
        this.accountID = account.getId();
        this.userID = account.getUserId();
        this.username = account.getUsername();
        this.email = account.getEmail();
        this.registeredDate = formatter.format(account.getCreatedDateTime());
    }

    public LoginSessionInfo(BoardAccountDTO accountDTO){
        this.accountID = accountDTO.getId();
        this.userID = accountDTO.getUserId();
        this.username = accountDTO.getUsername();
        this.email = accountDTO.getEmail();
        this.registeredDate = accountDTO.getRegisteredDate();
    }
}
