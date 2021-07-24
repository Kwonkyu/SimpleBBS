package com.haruhiism.bbs.command.manage;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BoardManagementCommand {

    @NotNull(message = "Board management operation should not be null.")
    BoardManagementCommand.Operation operation;

    List<Long> target = new ArrayList<>();

    public enum Operation {
        DELETE, RESTORE
    }
}
