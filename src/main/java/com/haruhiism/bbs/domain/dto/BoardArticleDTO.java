package com.haruhiism.bbs.domain.dto;

import com.haruhiism.bbs.domain.entity.BoardArticle;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Builder
@AllArgsConstructor
// TODO: @Builder와 커스텀 생성자는 같이 존재할 수 없는듯. @AllArgsConstructor가 명시적으로 필요한 듯 한데 이에 대해서 조사 및 기록.
public class BoardArticleDTO {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long id;
    private String writer;
    private String password;
    private String title;
    private String content;
    private boolean isWrittenByAccount;
    private String createdDate;
    private String modifiedDate;

    public BoardArticleDTO(BoardArticle article){
        this.id = article.getId();
        this.writer = article.getWriter();
        this.password = article.getPassword();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.isWrittenByAccount = (article.getBoardAccount() != null);

        LocalDateTime createdDateTime = article.getCreatedDateTime();
        if(createdDateTime != null){
            this.createdDate = formatter.format(createdDateTime);
        }
        LocalDateTime modifiedDateTime = article.getModifiedDateTime();
        if(modifiedDateTime != null) {
            this.modifiedDate = formatter.format(modifiedDateTime);
        }
    }

    @Override
    public String toString() {
        return String.format("[#%d] '%s' written by '%s'(account=%s).\nContents: %10s...\n", id, title, writer, isWrittenByAccount, content);
    }
}
