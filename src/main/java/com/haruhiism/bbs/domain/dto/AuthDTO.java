package com.haruhiism.bbs.domain.dto;


import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthDTO {
    private String rawPassword;
    private LoginSessionInfo loginSessionInfo;
}
