package com.haruhiism.bbs.domain;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class BoardSubmitCommand {

    @NotBlank
    @Length(max = 64)
    private String writer;
    @NotBlank
    @Length(max=256)
    private String title;
    @NotBlank
    @Length(max=65535)
    private String content;

}
