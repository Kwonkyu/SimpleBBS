package com.haruhiism.bbs.domain.command.article;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class ArticleListCommand {

    @NotNull
    @PositiveOrZero
    private int pageNum = 0;
    @NotNull
    @Min(1)
    private int pageSize = 10;

}
