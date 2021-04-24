package com.haruhiism.bbs.command.article;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ArticleEditSubmitCommand extends ArticleSubmitCommand{
    @NotNull
    @Positive
    private Long articleID;

    // TODO: 추가, 삭제된 파일 필드 포함.
    private List<String> delete = new ArrayList<>();
}
