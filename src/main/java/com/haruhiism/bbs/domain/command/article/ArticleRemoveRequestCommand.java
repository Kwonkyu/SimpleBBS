package com.haruhiism.bbs.domain.command.article;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.Positive;

@Getter
@Setter
public class ArticleRemoveRequestCommand {
    @NonNull
    @Positive
    private Long id;
}
