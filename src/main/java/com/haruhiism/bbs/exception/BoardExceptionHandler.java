package com.haruhiism.bbs.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class BoardExceptionHandler {

    @ExceptionHandler(NoArticleFoundException.class)
    public String noArticleFound(){
        return "redirect:/board/list";
    }

}
