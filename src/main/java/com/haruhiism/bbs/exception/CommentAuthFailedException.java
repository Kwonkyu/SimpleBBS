package com.haruhiism.bbs.exception;

public class CommentAuthFailedException extends RuntimeException {
    public String errorTitle = "Requested Operation Not Permitted.";
    public String errorDescription = "You don't have privilege to edit this comment.";
}
