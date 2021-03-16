package com.haruhiism.bbs.domain.entity;

import java.util.List;

public class ArticleAndComments {
    public BoardArticle article;
    public List<BoardComment> comments;
    public int commentSize;

    public ArticleAndComments(BoardArticle article, int commentSize){
        this.article = article;
        this.commentSize = commentSize;
    }

    public ArticleAndComments(BoardArticle article, List<BoardComment> comments){
        this.article = article;
        this.comments = comments;
        this.commentSize = comments.size();
    }
}
