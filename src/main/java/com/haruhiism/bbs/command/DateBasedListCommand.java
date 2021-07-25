package com.haruhiism.bbs.command;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class DateBasedListCommand extends ListCommand{
    private boolean betweenDates = false;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate from = LocalDate.of(1970, 1, 1);

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate to = LocalDate.now().plusDays(1);
}
