package com.haruhiism.bbs.command.article;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class ArticleReadCommand {
    @NotNull
    @Positive
    private Long id;
    @PositiveOrZero
    private int commentPage = 0;
}
