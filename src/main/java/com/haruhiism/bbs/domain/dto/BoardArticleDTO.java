package com.haruhiism.bbs.domain.dto;

import lombok.*;

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

}
