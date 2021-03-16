package com.haruhiism.bbs.domain.command.article;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Setter
@Getter
public class ArticleEditAuthCommand {

    @NotNull
    @Positive
    private Long articleID;
    @NotBlank
    private String password;
}
