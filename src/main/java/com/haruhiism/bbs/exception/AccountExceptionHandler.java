package com.haruhiism.bbs.exception;

import com.haruhiism.bbs.exception.account.NoAccountFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AccountExceptionHandler {

    @ExceptionHandler(NoAccountFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noAccountFound(Model model, NoAccountFoundException exception){
        model.addAttribute("errorTitle", exception.errorTitle);
        model.addAttribute("errorDescription", exception.errorDescription);
        return "error/request-failed";
    }
}
