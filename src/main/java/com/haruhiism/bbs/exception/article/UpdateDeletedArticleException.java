package com.haruhiism.bbs.exception.article;

public class UpdateDeletedArticleException extends RuntimeException {
    public String errorTitle = "Update Article Not Found.";
    public String errorDescription = "Article to update is not found. Maybe article is already deleted.";
}
