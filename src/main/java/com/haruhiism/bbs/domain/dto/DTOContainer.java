package com.haruhiism.bbs.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DTOContainer {
    protected int currentPage;
    protected int totalPages;
}
