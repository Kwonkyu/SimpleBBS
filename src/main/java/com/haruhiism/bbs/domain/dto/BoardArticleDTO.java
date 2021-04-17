package com.haruhiism.bbs.domain.dto;

import com.haruhiism.bbs.domain.entity.BoardArticle;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
// TODO: @Builder와 커스텀 생성자는 같이 존재할 수 없는듯. @AllArgsConstructor가 명시적으로 필요한 듯 한데 이에 대해서 조사 및 기록.
public class BoardArticleDTO {

    private Long id;
    private String writer;
    private String password;
    private String title;
    private String content;
    private boolean isWrittenByAccount;

    public BoardArticleDTO(BoardArticle article){
        this.id = article.getId();
        this.writer = article.getWriter();
        this.password = article.getPassword();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.isWrittenByAccount = (article.getBoardAccount() != null);
    }

    @Override
    public String toString() {
        return String.format("[#%d] '%s' written by '%s'(account=%s).\nContents: %10s...\n", id, title, writer, isWrittenByAccount, content);
    }
}
