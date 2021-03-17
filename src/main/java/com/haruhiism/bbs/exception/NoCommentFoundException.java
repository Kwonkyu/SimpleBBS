package com.haruhiism.bbs.exception;

public class NoCommentFoundException extends RuntimeException {
    public String errorTitle = "Request Comment Not Found.";
    public String errorDescription = "Comment to remove is not found. Maybe comment is already deleted.";
}
