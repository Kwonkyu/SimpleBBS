package com.haruhiism.bbs.command.account;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RegisterRequestCommand {
    @NotBlank
    private String userid;
    @NotBlank
    private String username;
    @NotBlank
    @Length(min = 4)
    private String password;
    @NotBlank
    @Email
    private String email;
}
