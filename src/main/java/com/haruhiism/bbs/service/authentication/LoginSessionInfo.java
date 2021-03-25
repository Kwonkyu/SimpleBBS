package com.haruhiism.bbs.service.authentication;

import com.haruhiism.bbs.domain.entity.BoardAccountLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class LoginSessionInfo {

    private final Long accountID;
    private final String userID;
    private final String username;
    private final String password;
    private final String email;
    private final List<BoardAccountLevel> levels;


}
