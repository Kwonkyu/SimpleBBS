package com.haruhiism.bbs.command.article;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.Positive;

@Getter
@Setter
public class ArticleEditRequestCommand {
    @NonNull
    @Positive
    private Long id;
}
