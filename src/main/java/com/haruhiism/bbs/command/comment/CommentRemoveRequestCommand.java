package com.haruhiism.bbs.command.comment;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class CommentRemoveRequestCommand {

    @NotNull(message = "Article ID cannot be null.", groups = {CommentRemoveRequestValidationGroup.class, CommentRemoveSubmitValidationGroup.class})
    @Positive(message = "Article ID cannot be negative or zero.", groups = {CommentRemoveRequestValidationGroup.class, CommentRemoveSubmitValidationGroup.class})
    private Long id;

    @NotBlank(message = "Password cannot be empty.", groups = {CommentRemoveSubmitValidationGroup.class})
    private String password;
}
