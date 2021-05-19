package com.haruhiism.bbs.command.article;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ArticleEditSubmitCommand extends ArticleSubmitCommand{
    @NotNull(message = "Article ID cannot be null.")
    @Positive(message = "Article ID cannot be negative or zero.")
    private Long id;

    private List<String> delete = new ArrayList<>();
}
