package com.haruhiism.bbs.domain.command.article;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class ArticleReadCommand {
    @NotNull
    @Positive
    private Long id;
}
