package com.haruhiism.bbs.domain.command.article;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Setter
@Getter
public class ArticleEditAuthCommand extends ArticleSubmitCommand {

    @NotNull
    @PositiveOrZero
    private Long articleID;
}
