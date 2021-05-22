package com.haruhiism.bbs.command.article;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class ArticleEditRequestCommand {

    @NotNull(message = "Article ID cannot be null.", groups = {ArticleEditRequestValidationGroup.class, ArticleEditSubmitValidationGroup.class})
    @Positive(message = "Article ID cannot be negative or zero.", groups = {ArticleEditRequestValidationGroup.class, ArticleEditSubmitValidationGroup.class})
    private Long id;

    @NotBlank(message = "Password cannot be null.", groups = {ArticleEditSubmitValidationGroup.class})
    private String password;
}
