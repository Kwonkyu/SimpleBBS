package com.haruhiism.bbs.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BoardAccountsDTO {

    private int currentPage;
    private int totalPage;

    private List<BoardAccountDTO> accounts;

}
