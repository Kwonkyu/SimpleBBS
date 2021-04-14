package com.haruhiism.bbs.command.article;

import com.haruhiism.bbs.domain.SearchMode;
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
