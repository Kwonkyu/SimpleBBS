package com.haruhiism.bbs.command.article;

import com.haruhiism.bbs.domain.SearchMode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class ArticleListCommand {

    @PositiveOrZero
    private int pageNum = 0;
    @Positive
    @Max(value = 40)
    private int pageSize = 10;
    @NotNull
    private SearchMode mode = SearchMode.TITLE;
    private String keyword = "";
}
