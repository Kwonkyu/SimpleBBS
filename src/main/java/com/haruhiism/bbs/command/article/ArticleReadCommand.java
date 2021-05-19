package com.haruhiism.bbs.command.article;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class ArticleReadCommand {
    @NotNull(message = "Article ID cannot be null.")
    @Positive(message = "Article ID cannot be negative.")
    private Long id;

    @PositiveOrZero(message = "Comment page cannot be negative or zero.")
    private int commentPage = 0;
}
