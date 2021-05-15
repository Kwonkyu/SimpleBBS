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
    private boolean isWrittenByAccount;
    private String createdDate;
    private String modifiedDate;
    private int hit;
    private boolean deleted;

    public BoardArticleDTO(BoardArticle article){
        BoardAccount writerAccount = article.getBoardAccount();

        this.id = article.getId();
        this.writer = writerAccount == null ? article.getWriter() : writerAccount.getUsername();
        this.password = article.getPassword();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.isWrittenByAccount = writerAccount != null;
        this.createdDate = formatter.format(article.getCreatedDateTime());
        this.modifiedDate = formatter.format(article.getModifiedDateTime());
        this.hit = article.getHit().getHit();
        this.deleted = article.isDeleted();
    }

    public BoardArticleDTO(ArticleSubmitCommand command){
        this.writer = command.getWriter();
        this.password = command.getPassword();
        this.title = command.getTitle();
        this.content = command.getContent();
    }

    @Override
    public String toString() {
        return String.format("[#%d] '%s' written by '%s'(account=%s).\nContents: %10s...\n", id, title, writer, isWrittenByAccount, content);
    }
}
