package com.haruhiism.bbs.command.account;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RegisterRequestCommand {
    @NotBlank(message = "ID cannot be empty.")
    private String userid;

    @NotBlank(message = "Name cannot be empty.")
    private String username;

    @NotBlank(message = "Password cannot be empty.")
    @Length(min = 4, message = "Password should be at least 4 characters.")
    private String password;

    @NotBlank(message = "Email cannot be empty.")
    @Email(message = "Email format should be valid.")
    private String email;

    @NotBlank(message = "Recovery question string cannot be empty.")
    private String recoveryQuestion;

    @NotBlank(message = "Recovery answer string cannot be empty.")
    private String recoveryAnswer;
}
