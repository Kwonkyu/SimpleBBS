package com.haruhiism.bbs.domain.dto;

import com.haruhiism.bbs.command.article.ArticleSubmitCommand;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BoardArticleDTO {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long id;
    private String writer;
    private String password;
    private String title;
    private String content;
    private String userId;
    private String createdDate;
    private String modifiedDate;
    private int hit;
    private boolean deleted;

    public BoardArticleDTO(BoardArticle article){
        BoardAccount writerAccount = article.getBoardAccount();

        id = article.getId();
        writer = article.getWriter();
        password = article.getPassword();
        title = article.getTitle();
        content = article.getContent();
        userId = writerAccount == null ? "" : writerAccount.getUserId();
        createdDate = formatter.format(article.getCreatedDateTime());
        modifiedDate = formatter.format(article.getModifiedDateTime());
        hit = article.getHit().getHit();
        deleted = article.isDeleted();
    }

    public BoardArticleDTO(ArticleSubmitCommand command){
        writer = command.getWriter();
        password = command.getPassword();
        title = command.getTitle();
        content = command.getContent();
    }

    @Override
    public String toString() {
        return String.format("[#%d] '%s' written by '%s'(account=%s).\nContents: %10s...\n", id, title, writer, userId, content);
    }
}
