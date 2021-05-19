package com.haruhiism.bbs.command.article;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class ArticleRemoveRequestCommand {
    @NotNull(message = "Article ID cannot be null.", groups = {ArticleRemoveRequestValidationGroup.class, ArticleRemoveSubmitValidationGroup.class})
    @Positive(message = "Article ID cannot be negative or zero.", groups = {ArticleRemoveRequestValidationGroup.class, ArticleRemoveSubmitValidationGroup.class})
    private Long id;

    @NotBlank(message = "Password cannot be blank.", groups = {ArticleRemoveSubmitValidationGroup.class})
    @Length(min = 4, message = "Password should be at least 4 characters.", groups = {ArticleRemoveSubmitValidationGroup.class})
    private String password;
}
