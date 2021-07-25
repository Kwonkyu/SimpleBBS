package com.haruhiism.bbs.command.manage;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AccountManagementCommand {

    @NotNull(message = "Account management operation should not be null.")
    AccountManagementCommand.Operation operation;

    List<Long> target = new ArrayList<>();

    String keyword;

    public enum Operation {
        CHANGE_PASSWORD, INVALIDATE, RESTORE, CHANGE_USERNAME
    }
}
