package com.haruhiism.bbs.command;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class ListCommand {
    @PositiveOrZero(message = "Page index can't be negative.")
    private int pageNum = 0;

    @Positive(message = "Page size can't be negative or zero.")
    @Max(value = 40, message = "Page size can't exceeds 40.")
    private int pageSize = 10;

    private String keyword = "";
}
