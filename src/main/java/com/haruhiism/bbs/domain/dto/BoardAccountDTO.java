package com.haruhiism.bbs.domain.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class BoardAccountDTO {

    @NonNull
    private String userId;
    @NonNull
    private String username;
    @NonNull
    private String rawPassword;
    @NonNull
    private String email;
}
