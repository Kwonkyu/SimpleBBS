package com.haruhiism.bbs.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class BoardAccountsDTO extends DTOContainer{

    private List<BoardAccountDTO> accounts;

    @Builder
    // https://www.baeldung.com/lombok-builder-inheritance
    public BoardAccountsDTO(int currentPage, int totalPages, List<BoardAccountDTO> accounts){
        super(currentPage, totalPages);
        this.accounts = accounts;
    }
}
