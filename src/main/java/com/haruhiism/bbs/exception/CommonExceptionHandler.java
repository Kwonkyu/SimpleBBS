package com.haruhiism.bbs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class CommonExceptionHandler {

    // MethodArgumentNotValidException for @Valid, BindException for @ModelAttribute
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public String methodArgumentNotValid(Model model, HttpServletResponse response){
        response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        model.addAttribute("errorTitle", "Transmitted Request Can Not Be Processed.");
        model.addAttribute("errorDescription", "Requested has incompatible parameter or something is wrong.");
        return "board/error/request-failed";
    }
}
