package com.haruhiism.bbs.domain.dto;

import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BoardCommentDTO {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long id;
    private Long articleId;
    private String writer;
    private String password;
    private String content;
    private String userId;
    private String createdDate;
    private boolean deleted;

    public BoardCommentDTO(BoardComment boardComment) {
        BoardAccount writerAccount = boardComment.getBoardAccount();

        id = boardComment.getId();
        articleId = boardComment.getBoardArticle().getId();
        writer = boardComment.getWriter();
        password = boardComment.getPassword();
        content = boardComment.getContent();
        userId = writerAccount == null ? "" : writerAccount.getUserId();
        deleted = boardComment.isDeleted();

        LocalDateTime createdDateTime = boardComment.getCreatedDateTime();
        if(createdDateTime != null){
            createdDate = formatter.format(createdDateTime);
        }
    }

    @Override
    public String toString() {
        return String.format("[#%d of Article #%d] Comment '%10s...' written by %s\n", id, articleId, content, writer);
    }
}
