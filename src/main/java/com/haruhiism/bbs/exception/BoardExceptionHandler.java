package com.haruhiism.bbs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class BoardExceptionHandler {

    @ExceptionHandler(NoArticleFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noArticleFound(Model model, NoArticleFoundException exception){
        model.addAttribute("errorTitle", exception.errorTitle);
        model.addAttribute("errorDescription", exception.errorDescription);
        return "board/error/request-failed";
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String articleEditAuthFailed(Model model, AuthenticationFailedException exception) {
        model.addAttribute("errorTitle", exception.errorTitle);
        model.addAttribute("errorDescription", exception.errorDescription);
        return "board/error/request-failed";
    }

    @ExceptionHandler(UpdateDeletedArticleException.class)
    @ResponseStatus(HttpStatus.GONE)
    public String updateDeletedArticle(Model model, UpdateDeletedArticleException exception){
        model.addAttribute("errorTitle", exception.errorTitle);
        model.addAttribute("errorDescription", exception.errorDescription);
        return "board/error/request-failed";
    }

    @ExceptionHandler(NoCommentFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noCommentFound(Model model, NoCommentFoundException exception) {
        model.addAttribute("errorTitle", exception.errorTitle);
        model.addAttribute("errorDescription", exception.errorDescription);
        return "board/error/request-failed";
    }

    @ExceptionHandler(CommentAuthFailedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String commentAuthFailed(Model model, CommentAuthFailedException exception) {
        model.addAttribute("errorTitle", exception.errorTitle);
        model.addAttribute("errorDescription", exception.errorDescription);
        return "board/error/request-failed";
    }
}
