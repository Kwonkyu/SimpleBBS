package com.haruhiism.bbs.exception;

public class NoAccountFoundException extends RuntimeException{
    public String errorTitle = "No Account Found";
    public String errorDescription = "There's no account with given profile.";
}
