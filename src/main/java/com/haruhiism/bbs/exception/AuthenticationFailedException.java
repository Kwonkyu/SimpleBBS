package com.haruhiism.bbs.exception;

public class AuthenticationFailedException extends RuntimeException {
    public String errorTitle = "Requested Operation Not Permitted.";
    public String errorDescription = "You don't have privilege to access requested resources.";
}
