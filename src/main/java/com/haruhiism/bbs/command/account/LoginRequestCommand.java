package com.haruhiism.bbs.command.account;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginRequestCommand {
    @NotBlank(message = "ID cannot be empty.")
    private String userid = "";
    @NotBlank(message = "Password cannot be empty.")
    private String password = "";
}
