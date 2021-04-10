package com.haruhiism.bbs.domain.dto;

import com.haruhiism.bbs.service.authentication.LoginSessionInfo;
import lombok.*;
import org.springframework.lang.Nullable;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class BoardArticleDTO {

    private Long id;
    @NonNull
    private String writer;
    @NonNull
    private String password;
    @NonNull
    private String title;
    @NonNull
    private String content;

    @Nullable
    private Long accountId;
}
