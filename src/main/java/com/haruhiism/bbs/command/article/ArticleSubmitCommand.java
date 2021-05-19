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

    @NotBlank(message = "Writer cannot be blank.")
    @Length(max = 64, message = "Writer cannot exceeds 64 characters.")
    private String writer;

    @NotBlank(message = "Password cannot be blank.")
    @Length(min = 4, message = "Password should be at least 4 characters.")
    private String password;

    @NotBlank(message = "Title cannot be blank.")
    @Length(max = 255, message = "Title cannot exceeds 255 characters.")
    private String title;

    @NotBlank(message = "Content cannot be blank.")
    @Length(max = 65535, message = "Content cannot exceeds 65535 characters.")
    private String content;

    private List<MultipartFile> uploadedFiles = new ArrayList<>();
}
