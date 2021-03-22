package com.haruhiism.bbs.command.article;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ArticleSubmitCommand {

    @NotBlank
    @Length(max = 64)
    private String writer;
    @NotBlank
    @Length(min = 4)
    private String password;
    @NotBlank
    @Length(max = 255)
    private String title;
    @NotBlank
    @Column(columnDefinition = "TEXT", length = 65536)
    @Length(max = 65535)
    private String content;

}
