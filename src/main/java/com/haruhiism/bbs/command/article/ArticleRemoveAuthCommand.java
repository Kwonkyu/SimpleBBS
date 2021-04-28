package com.haruhiism.bbs.command.article;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class ArticleRemoveAuthCommand {

    @NotNull
    @Positive
    private Long id;
    @NotBlank
    @Length(min = 4)
    private String password;
}
