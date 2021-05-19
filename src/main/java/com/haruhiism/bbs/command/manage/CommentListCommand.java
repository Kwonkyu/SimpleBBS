package com.haruhiism.bbs.command.manage;

import com.haruhiism.bbs.command.DateBasedListCommand;
import com.haruhiism.bbs.domain.CommentSearchMode;
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
public class CommentListCommand extends DateBasedListCommand {

    @NotNull(message = "Search mode cannot be null.")
    CommentSearchMode mode = CommentSearchMode.CONTENT;
}
