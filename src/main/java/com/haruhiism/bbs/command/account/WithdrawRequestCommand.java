package com.haruhiism.bbs.command.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class WithdrawRequestCommand {
    @NotBlank
    private final String password;
}
