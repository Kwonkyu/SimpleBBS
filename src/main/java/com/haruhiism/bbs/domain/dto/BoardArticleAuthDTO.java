package com.haruhiism.bbs.domain.dto;


import com.haruhiism.bbs.service.authentication.LoginSessionInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BoardArticleAuthDTO {

    private Long articleId;
    private String rawPassword;
    private LoginSessionInfo loginSessionInfo;
}
