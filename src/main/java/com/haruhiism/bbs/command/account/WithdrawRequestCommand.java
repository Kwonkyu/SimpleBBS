package com.haruhiism.bbs.command.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class WithdrawRequestCommand {
    @NotBlank(message = "Password should not empty.")
    private String password = "";
}
