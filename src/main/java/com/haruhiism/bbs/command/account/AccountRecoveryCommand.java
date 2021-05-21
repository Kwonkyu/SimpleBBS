package com.haruhiism.bbs.command.account;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class AccountRecoveryCommand {

    @NotBlank(message = "User ID cannot be empty.", groups = {AccountRecoveryRequestValidationGroup.class, AccountRecoverySubmitValidationGroup.class})
    private String userId = "";

    @NotBlank(message = "Answer cannot be empty.", groups = {AccountRecoverySubmitValidationGroup.class})
    private String answer = "";

    @NotBlank(message = "New password cannot be empty.", groups = {AccountRecoverySubmitValidationGroup.class})
    private String newPassword = "";
}
