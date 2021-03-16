package com.haruhiism.bbs.domain.command.article;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Setter
@Getter
public class ArticleEditAuthCommand extends ArticleSubmitCommand {

    @NotNull
    @Positive
    private Long articleID;
}
