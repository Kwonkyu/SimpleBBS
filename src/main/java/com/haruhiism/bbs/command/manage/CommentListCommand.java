package com.haruhiism.bbs.command.manage;

import com.haruhiism.bbs.command.DateBasedListCommand;
import com.haruhiism.bbs.domain.CommentSearchMode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CommentListCommand extends DateBasedListCommand {
    @NotNull(message = "Search mode cannot be null.")
    CommentSearchMode mode = CommentSearchMode.CONTENT;
}
