package com.haruhiism.bbs.command.article;

import com.haruhiism.bbs.command.DateBasedListCommand;
import com.haruhiism.bbs.domain.ArticleSearchMode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ArticleListCommand extends DateBasedListCommand {

    @NotNull(message = "Search mode cannot be null.")
    private ArticleSearchMode mode = ArticleSearchMode.TITLE;
}
