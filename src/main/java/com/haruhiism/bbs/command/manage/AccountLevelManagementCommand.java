package com.haruhiism.bbs.command.manage;

import com.haruhiism.bbs.domain.ManagerLevel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AccountLevelManagementCommand {

    @NotBlank(message = "Account ID cannot be empty.", groups = {AccountLevelManagementRequestValidationGroup.class, AccountLevelManagementSubmitValidationGroup.class})
    private String id;

    @NotNull(message = "Level cannot be null.", groups = {AccountLevelManagementSubmitValidationGroup.class})
    private ManagerLevel levelName;

    @NotNull(message = "Operation cannot be null.", groups = {AccountLevelManagementSubmitValidationGroup.class})
    private AccountLevelManagementOperation operation;
}
