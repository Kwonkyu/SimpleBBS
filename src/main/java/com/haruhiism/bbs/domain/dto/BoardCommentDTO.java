package com.haruhiism.bbs.domain.dto;

import com.haruhiism.bbs.domain.entity.BoardComment;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BoardCommentDTO {

    private Long id;
    private Long articleID;
    private String writer;
    private String password;
    private String content;
    // TODO: 나중에 상속 관계로 분리?
    private boolean isWrittenByAccount;

    public BoardCommentDTO(BoardComment boardComment) {
        this.id = boardComment.getId();
        this.articleID = boardComment.getBoardArticle().getId();
        this.writer = boardComment.getWriter();
        this.password = boardComment.getPassword();
        this.content = boardComment.getContent();
        isWrittenByAccount = boardComment.getBoardAccount() != null;
    }

    @Override
    public String toString() {
        return String.format("[#%d of Article #%d] Comment '%10s...' written by %s\n", id, articleID, content, writer);
    }
}
