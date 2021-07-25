package com.haruhiism.bbs.domain.dto;

import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardComment;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
public class BoardCommentDTO {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Getter
    public static class PagedComments {
        private final int currentPage;
        private final int pages;
        private final List<BoardCommentDTO.Read> comments;

        public PagedComments(Page<BoardComment> comments) {
            this.currentPage = comments.getNumber();
            this.pages = comments.getTotalPages();
            this.comments = comments.map(BoardCommentDTO.Read::new).toList();
        }
    }

    @Getter
    @Setter
    public static class Read {
        private long id;
        private long articleId;
        private String writer;
        private String content;
        private String userId;
        private String createdDate;
        private boolean deleted;

        public Read(BoardComment boardComment) {
            BoardAccount account = boardComment.getBoardAccount();

            id = boardComment.getId();
            articleId = boardComment.getBoardArticle().getId();
            writer = boardComment.getWriter();
            content = boardComment.getContent();
            userId = account == null ? "" : account.getUserId();
            deleted = boardComment.isDeleted();
            createdDate = formatter.format(boardComment.getCreatedDateTime());
        }

        public boolean isAnonymous() { return userId.isBlank(); }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Authorize {
        @Positive(message = "Comment ID cannot be negative or zero.", groups = {Request.class, Submit.class})
        private long id;

        @NotBlank(message = "Password cannot be empty.", groups = {Submit.class})
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
        @Positive(message = "Comment ID cannot be negative or zero.")
        private long id;

        @Positive(message = "Article ID cannot be negative or zero.", groups = Create.class)
        private long articleId;

        @NotBlank(message = "Writer cannot be empty.", groups = Create.class)
        @Length(max = 64, message = "Writer cannot exceeds 64 characters.", groups = Create.class)
        private String writer;

        @NotBlank(message = "Password cannot be empty.", groups = {Create.class})
        @Length(min = 4, message = "Password should be at least 4 characters.", groups = {Create.class})
        private String password;

        @NotBlank(message = "Content cannot be empty.", groups = Create.class)
        @Length(max = 255, message = "Content cannot exceeds 255 characters.", groups = Create.class)
        private String content;

        public interface Create {}

        public void encodePassword(PasswordEncoder encoder) {
            this.password = encoder.encode(password);
        }
        public void encodePassword(PasswordEncoder encoder, String newPassword) {
            this.password = encoder.encode(newPassword);
        }
    }

    public enum CommentSearchMode {
        ARTICLE, WRITER, CONTENT, ACCOUNT;
    }
}
