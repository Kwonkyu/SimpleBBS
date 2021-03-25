package com.haruhiism.bbs.command.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@RequiredArgsConstructor
public class InfoUpdateRequestCommand {

    @NotNull
    private final UpdatableInformation mode;

}
