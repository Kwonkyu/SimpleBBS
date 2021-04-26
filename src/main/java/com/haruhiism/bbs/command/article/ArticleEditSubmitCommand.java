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
    @NotNull
    @Positive
    private Long articleID;

    private List<String> delete = new ArrayList<>();
}
