package com.haruhiism.bbs.command.account;

import com.haruhiism.bbs.domain.UpdatableInformation;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class InfoUpdateSubmitCommand {
    @NotNull
    private final UpdatableInformation mode;
    @NotBlank
    private final String auth;
    @NotBlank
    private final String previous;
    @NotBlank
    private final String updated;
}
