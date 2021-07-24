package com.haruhiism.bbs.command.manage;

import com.haruhiism.bbs.command.DateBasedListCommand;
import com.haruhiism.bbs.domain.dto.BoardCommentDTO;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CommentListCommand extends DateBasedListCommand {
    @NotNull(message = "Search mode cannot be null.")
    BoardCommentDTO.CommentSearchMode mode = BoardCommentDTO.CommentSearchMode.CONTENT;
}
