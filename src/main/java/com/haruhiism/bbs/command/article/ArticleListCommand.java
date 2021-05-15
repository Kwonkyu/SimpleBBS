package com.haruhiism.bbs.command.article;

import com.haruhiism.bbs.domain.ArticleSearchMode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Getter
@Setter
public class ArticleListCommand {

    @PositiveOrZero
    private int pageNum = 0;
    @Positive
    @Max(value = 40)
    private int pageSize = 10;
    private ArticleSearchMode mode = ArticleSearchMode.TITLE;
    private String keyword = "";
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate from;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate to;
    private boolean betweenDates = false;
}
