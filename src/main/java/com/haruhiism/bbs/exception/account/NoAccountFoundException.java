package com.haruhiism.bbs.exception.account;

public class NoAccountFoundException extends RuntimeException{
    public String errorTitle = "No Account Found";
    public String errorDescription = "There's no account with given profile.";
}
