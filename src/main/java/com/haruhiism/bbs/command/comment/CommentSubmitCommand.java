package com.haruhiism.bbs.command.comment;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Setter
@Getter
public class CommentSubmitCommand {

    @NotNull(message = "Article ID cannot be null.")
    @Positive(message = "Article ID cannot be negative or zero.")
    private Long articleID;

    @NotBlank(message = "Writer cannot be empty.")
    @Length(max = 64, message = "Writer cannot exceeds 64 characters.")
    private String writer;

    @NotBlank(message = "Password cannot be empty.")
    @Length(min = 4, message = "Password should be at least 4 characters.")
    private String password;

    @NotBlank(message = "Content cannot be empty.")
    @Length(max = 255, message = "Content cannot exceeds 255 characters.")
    private String content;


}
