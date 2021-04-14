package com.haruhiism.bbs.command.article;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class ArticleListCommand {

    @PositiveOrZero
    private int pageNum = 0;
    @Positive
    @Max(value = 30)
    private int pageSize = 10;
}
