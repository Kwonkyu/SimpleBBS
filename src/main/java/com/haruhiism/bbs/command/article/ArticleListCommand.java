package com.haruhiism.bbs.command.article;

import com.haruhiism.bbs.domain.SearchMode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

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
