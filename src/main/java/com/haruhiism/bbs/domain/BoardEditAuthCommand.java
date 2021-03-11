package com.haruhiism.bbs.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class BoardEditAuthCommand extends BoardSubmitCommand {

    @NotBlank
    private Long bid;
}
