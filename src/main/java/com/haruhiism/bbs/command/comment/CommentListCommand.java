package com.haruhiism.bbs.command.comment;

import com.haruhiism.bbs.domain.CommentSearchMode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Getter
@Setter
public class CommentListCommand {

    @PositiveOrZero
    private int pageNum = 0;
    @Positive
    @Max(40)
    private int pageSize = 10;
    CommentSearchMode mode = CommentSearchMode.CONTENT;
    private String keyword = "";
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate from = LocalDate.of(1970,1,1);
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate to = LocalDate.now();
    private boolean betweenDates = false;
}
