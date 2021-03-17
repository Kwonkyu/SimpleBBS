package com.haruhiism.bbs.domain.command.article;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ArticleSearchCommand extends ArticleListCommand {
    @NotNull
    private SearchMode mode;
    @NotBlank
    private String keyword;
}
