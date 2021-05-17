package com.haruhiism.bbs.command.account;

import com.haruhiism.bbs.domain.AccountSearchMode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Getter
@Setter
public class AccountListCommand {

    @PositiveOrZero
    private int pageNum = 0;
    @Positive
    private int pageSize = 10;
    private AccountSearchMode mode = AccountSearchMode.USERNAME;
    private String keyword = "";
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate from = LocalDate.of(1970,1,1);
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate to = LocalDate.now();
    private boolean betweenDates = false;
}
