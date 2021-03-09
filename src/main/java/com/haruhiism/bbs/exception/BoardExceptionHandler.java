package com.haruhiism.bbs.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class BoardExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public String noHandlerFound(){
        return "redirect:/not-available.html";
    }
}
