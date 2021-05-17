package com.haruhiism.bbs.command.manage;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BoardManagementCommand {

    @NotNull
    BoardManagementOperation operation;

    List<Long> target = new ArrayList<>();
}
