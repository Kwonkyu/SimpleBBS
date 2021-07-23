package com.haruhiism.bbs.domain.dto;

import com.haruhiism.bbs.command.article.ArticleEditSubmitCommand;
import com.haruhiism.bbs.command.article.ArticleSubmitCommand;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class BoardArticleDTO {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Getter
    public static class PagedArticles {
        private final int currentPage;
        private final int pages;
        private final List<BoardArticleDTO> articles;

        public PagedArticles(Page<BoardArticle> articles) {
            this.currentPage = articles.getNumber();
            this.pages = articles.getTotalPages();
            this.articles = articles.map(BoardArticleDTO::new).toList();
        }
    }

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
    private int comments;

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
        comments = article.getComments().size();
    }

    public BoardArticleDTO(ArticleSubmitCommand command){
        writer = command.getWriter();
        password = command.getPassword();
        title = command.getTitle();
        content = command.getContent();
    }

    public BoardArticleDTO(ArticleEditSubmitCommand command) {
        id = command.getId();
        writer = command.getWriter();
        password = command.getPassword();
        title = command.getTitle();
        content = command.getContent();
    }

    public boolean isWrittenByAccount() {
        return !this.userId.isBlank();
    }

    public void encodePassword(PasswordEncoder encoder) {
        password = encoder.encode(password);
    }
    public void encodePassword(PasswordEncoder encoder, String newPassword) {
        password = encoder.encode(newPassword);
    }

    @Override
    public String toString() {
        return String.format("[#%d] '%s' written by '%s'(account=%s).\nContents: %10s...\n", id, title, writer, userId, content);
    }
}
