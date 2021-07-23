package com.haruhiism.bbs.domain.dto;

import com.haruhiism.bbs.command.comment.CommentSubmitCommand;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class BoardCommentDTO {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Getter
    public static class PagedComments {
        private final int currentPage;
        private final int pages;
        private final List<BoardCommentDTO> comments;

        public PagedComments(Page<BoardComment> comments) {
            this.currentPage = comments.getNumber();
            this.pages = comments.getTotalPages();
            this.comments = comments.map(BoardCommentDTO::new).toList();
        }
    }

    private Long id;
    private final Long articleId;
    private String writer;
    private String password;
    private String content;
    private String userId;
    private String createdDate;
    private boolean deleted;

    public boolean isWrittenByAccount() {
        return !this.userId.isBlank();
    }

    public void updateWriter(String writer) {
        this.writer = writer;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }
    public void encodePassword(PasswordEncoder passwordEncoder, String newPassword) {
        this.password = passwordEncoder.encode(newPassword);
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public BoardCommentDTO(BoardComment boardComment) {
        BoardAccount writerAccount = boardComment.getBoardAccount();

        id = boardComment.getId();
        articleId = boardComment.getBoardArticle().getId();
        writer = boardComment.getWriter();
        password = "THIS_IS_MEANINGLESS_PASSWORD";
        content = boardComment.getContent();
        userId = writerAccount == null ? "" : writerAccount.getUserId();
        deleted = boardComment.isDeleted();

        LocalDateTime createdDateTime = boardComment.getCreatedDateTime();
        if(createdDateTime != null){
            createdDate = formatter.format(createdDateTime);
        }
    }

    public BoardCommentDTO(CommentSubmitCommand command) {
        this.articleId = command.getArticleId();
        this.writer = command.getWriter();
        this.password = command.getPassword();
        this.content = command.getContent();
    }
}
