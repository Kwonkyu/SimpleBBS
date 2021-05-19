package com.haruhiism.bbs.command.article;

import com.haruhiism.bbs.command.DateBasedListCommand;
import com.haruhiism.bbs.command.ListCommand;
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
public class ArticleListCommand extends DateBasedListCommand {

    @NotNull(message = "Search mode cannot be null.")
    private ArticleSearchMode mode = ArticleSearchMode.TITLE;
}
