package com.haruhiism.bbs.command.account;

import com.haruhiism.bbs.command.manage.AccountManagementOperation;
import com.haruhiism.bbs.domain.AccountSearchMode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class AccountListCommand {

    @PositiveOrZero
    private int pageNum = 0;
    @Positive
    private int pageSize = 10;
    private AccountSearchMode mode = AccountSearchMode.USERNAME;
    private String keyword = "";
    private String date;
}
