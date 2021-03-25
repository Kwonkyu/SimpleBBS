package com.haruhiism.bbs.command.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InfoUpdateSubmitCommand {

    private final UpdatableInformation mode;
    private final String auth;
    private final String previous;
    private final String updated;
}
