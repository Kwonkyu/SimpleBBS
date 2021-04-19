package com.haruhiism.bbs.domain.dto;

import com.haruhiism.bbs.domain.entity.BoardComment;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BoardCommentDTO {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long id;
    private Long articleID;
    private String writer;
    private String password;
    private String content;
    private boolean isWrittenByAccount;
    private String createdDate;

    public BoardCommentDTO(BoardComment boardComment) {
        this.id = boardComment.getId();
        this.articleID = boardComment.getBoardArticle().getId();
        this.writer = boardComment.getWriter();
        this.password = boardComment.getPassword();
        this.content = boardComment.getContent();
        isWrittenByAccount = boardComment.getBoardAccount() != null;

        LocalDateTime createdDateTime = boardComment.getCreatedDateTime();
        if(createdDateTime != null){
            this.createdDate = formatter.format(createdDateTime);
        }
    }

    @Override
    public String toString() {
        return String.format("[#%d of Article #%d] Comment '%10s...' written by %s\n", id, articleID, content, writer);
    }
}
