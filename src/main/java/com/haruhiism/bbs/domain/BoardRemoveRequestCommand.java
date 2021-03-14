package com.haruhiism.bbs.domain;


import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class BoardRemoveRequestCommand {

    private Long bid;

    private String password;
}
