package com.haruhiism.bbs.exception;

import com.haruhiism.bbs.exception.auth.AuthenticationFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.thymeleaf.exceptions.TemplateInputException;

@ControllerAdvice
public class CommonExceptionHandler {

    // MethodArgumentNotValidException for @Valid, BindException for @ModelAttribute
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String methodArgumentNotValid(Model model) {
        model.addAttribute("errorTitle", "Transmitted Request Can Not Be Processed.");
        model.addAttribute("errorDescription", "Request has incompatible parameter or something is wrong.");
        return "error/request-failed";
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String articleEditAuthFailed(Model model, AuthenticationFailedException exception) {
        model.addAttribute("errorTitle", exception.errorTitle);
        model.addAttribute("errorDescription", exception.errorDescription);
        return "error/request-failed";
    }

    @ExceptionHandler(TemplateInputException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String templateInput(Model model, TemplateInputException exception) {
        model.addAttribute("errorTitle", "Thymeleaf template has not found.");
        model.addAttribute("errorDescription", exception.getLocalizedMessage());
        return "error/request-failed";
    }
}
