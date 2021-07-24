package com.haruhiism.bbs.command.manage;

import com.haruhiism.bbs.command.DateBasedListCommand;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AccountListCommand extends DateBasedListCommand {
    @NotNull(message = "Search mode cannot be null.")
    private BoardAccountDTO.AccountSearchMode mode = BoardAccountDTO.AccountSearchMode.USERNAME;
}
