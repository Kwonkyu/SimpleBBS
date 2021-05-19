package com.haruhiism.bbs.command.account;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class AccountManageListCommand {

    @PositiveOrZero(message = "Page index cannot be negative.")
    private int articlePage = 0;

    @PositiveOrZero(message = "Page index cannot be negative.")
    private int commentPage = 0;
}
