package com.haruhiism.bbs.domain.command.comment;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Setter
@Getter
public class CommentSubmitCommand {

    @NotNull
    @Positive
    private Long articleID;
    @NotBlank
    @Length(max = 64)
    private String writer;
    @NotBlank
    @Length(min = 4)
    private String password;
    @NotBlank
    @Length(max = 255)
    private String content;


}
