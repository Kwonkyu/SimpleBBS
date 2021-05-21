package com.haruhiism.bbs.domain.dto;

import com.haruhiism.bbs.domain.ManagerLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BoardAccountLevelDTO {

    private String userId;
    private List<ManagerLevel> levels;
}
