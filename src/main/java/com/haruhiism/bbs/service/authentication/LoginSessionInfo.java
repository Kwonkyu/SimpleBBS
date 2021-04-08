package com.haruhiism.bbs.service.authentication;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginSessionInfo {

    private final Long accountID;
    private final String userID;
    private final String username;
    private final String password;
    private final String email;

}
