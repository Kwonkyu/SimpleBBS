package com.haruhiism.bbs.exception;

public class NoArticleFoundException extends RuntimeException {
    public String errorTitle = "Requested Article Not Found.";
    public String errorDescription = "Requested Article is deleted or does not exist.";
}
