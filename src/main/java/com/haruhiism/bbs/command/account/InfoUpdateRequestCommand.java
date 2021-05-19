package com.haruhiism.bbs.command.account;

import com.haruhiism.bbs.domain.UpdatableInformation;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class InfoUpdateRequestCommand {

    @NotNull(message = "Pick at least one information to change")
    private UpdatableInformation mode;

    @NotBlank(message = "Authentication string should not be empty")
    private String auth = "";

    private String previous = "";

    @NotBlank(message = "Updated string should not be empty")
    private String updated = "";
}
