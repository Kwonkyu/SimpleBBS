package com.haruhiism.bbs.command.article;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

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
    @Length(min = 4)
    private String password;
}
