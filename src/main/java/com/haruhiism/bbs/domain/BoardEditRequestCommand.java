package com.haruhiism.bbs.domain;


import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class BoardEditRequestCommand {

    @NonNull
    private Long bid;
}
