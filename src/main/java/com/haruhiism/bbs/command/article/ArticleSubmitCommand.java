package com.haruhiism.bbs.command.article;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ArticleSubmitCommand {

    @NotBlank @Length(max = 64)
    private String writer;
    @NotBlank @Length(min = 4)
    private String password;
    @NotBlank @Length(max = 255)
    private String title;
    @NotBlank @Length(max = 65535)
    private String content;
    private List<MultipartFile> uploadedFiles = new ArrayList<>();
}
