package com.haruhiism.bbs.exception;

public class InvalidFileException extends RuntimeException{
    public String errorTitle = "No Resource or File Found";
    public String errorDescription = "There's no resource or file with given profile.";
}
