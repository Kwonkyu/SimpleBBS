package com.haruhiism.bbs.command;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class ListCommand {
    @PositiveOrZero
    private int pageNum = 0;

    @Positive @Max(40)
    private int pageSize = 10;
}
