package com.haruhiism.bbs.domain.command.article;


import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class ArticleRemoveRequestCommand {

    @NotNull
    @PositiveOrZero
    private Long articleID;
    @NotBlank
    private String password;
}
