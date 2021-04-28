package com.haruhiism.bbs.command.comment;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class CommentRemoveRequestCommand {

    @NotNull
    @Positive
    private Long id;
    @NotBlank
    private String password;
}
