package com.haruhiism.bbs.domain.dto;

import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BoardArticleDTO {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Getter
    public static class PagedArticles {
        private final int currentPage;
        private final int pages;
        private final List<BoardArticleDTO.Read> articles;

        public PagedArticles(Page<BoardArticle> articles) {
            this.currentPage = articles.getNumber();
            this.pages = articles.getTotalPages();
            this.articles = articles.map(BoardArticleDTO.Read::new).toList();
        }
    }

    @Getter
    @Setter
    public static class Read {
        public long id;
        public String writer;
        public String title;
        public String content;
        public String userId;
        public String createdDate;
        public String modifiedDate;
        public int hit;
        public boolean deleted;
        public int comments;

        public Read(BoardArticle article) {
            BoardAccount account = article.getBoardAccount();

            id = article.getId();
            writer = article.getWriter();
            title = article.getTitle();
            content = article.getContent();
            userId = account == null ? "" : account.getUserId();
            createdDate = formatter.format(article.getCreatedDateTime());
            modifiedDate = formatter.format(article.getModifiedDateTime());
            hit = article.getHit().getHit();
            deleted = article.isDeleted();
            comments = article.getComments().size();
        }

        public boolean isAnonymous() { return userId.isBlank(); }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Authorize {
        @Positive(message = "Article ID cannot be negative or zero.", groups = {Request.class, Submit.class})
        private long id;

        @NotBlank(message = "Password cannot be blank.", groups = {Submit.class})
        @Length(min = 4, message = "Password should be at least 4 characters.", groups = {Submit.class})
        private String password;

        public interface Request {}
        public interface Submit {}
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Submit {
        @Positive(message = "Article ID cannot be negative or zero.", groups = {Update.class})
        private long id;

        @NotBlank(message = "Writer cannot be blank.", groups = {Create.class, Update.class})
        @Length(max = 64, message = "Writer cannot exceeds 64 characters.", groups = {Create.class, Update.class})
        private String writer;

        @NotBlank(message = "Password cannot be blank.", groups = {Create.class, Update.class})
        @Length(min = 4, message = "Password should be at least 4 characters.", groups = {Create.class, Update.class})
        private String password;

        @NotBlank(message = "Title cannot be blank.", groups = {Create.class, Update.class})
        @Length(max = 255, message = "Title cannot exceeds 255 characters.", groups = {Create.class, Update.class})
        private String title;

        @NotBlank(message = "Content cannot be blank.", groups = {Create.class, Update.class})
        @Length(max = 65535, message = "Content cannot exceeds 65535 characters.", groups = {Create.class, Update.class})
        private String content;

        private final List<MultipartFile> uploadedFiles = new ArrayList<>();
        private final List<String> delete = new ArrayList<>();

        public interface Create {}
        public interface Update {}

        public void encodePassword(PasswordEncoder encoder) {
            this.password = encoder.encode(password);
        }
        public void encodePassword(PasswordEncoder encoder, String newPassword) {
            this.password = encoder.encode(newPassword);
        }
    }

    public enum ArticleSearchMode {
        WRITER, ACCOUNT, TITLE, CONTENT, TITLE_CONTENT
    }
}
