package com.haruhiism.bbs.exception;

public class ArticleAuthFailedException extends RuntimeException {
    public String errorTitle = "Requested Operation Not Permitted.";
    public String errorDescription = "You don't have privilege to edit this article.";
}
