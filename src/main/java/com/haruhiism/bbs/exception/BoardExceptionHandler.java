package com.haruhiism.bbs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class BoardExceptionHandler {

    @ExceptionHandler(NoArticleFoundException.class)
    public String noArticleFound(Model model, HttpServletResponse response, NoArticleFoundException exception){
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("errorTitle", exception.errorTitle);
        model.addAttribute("errorDescription", exception.errorDescription);
        return "board/error/request-failed";
    }

    @ExceptionHandler(ArticleAuthFailedException.class)
    public String articleEditAuthFailed(Model model, HttpServletResponse response, ArticleAuthFailedException exception) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        model.addAttribute("errorTitle", exception.errorTitle);
        model.addAttribute("errorDescription", exception.errorDescription);
        return "board/error/request-failed";
    }

    @ExceptionHandler(UpdateDeletedArticleException.class)
    public String updateDeletedArticle(Model model, HttpServletResponse response, UpdateDeletedArticleException exception){
        response.setStatus(HttpStatus.GONE.value());
        model.addAttribute("errorTitle", exception.errorTitle);
        model.addAttribute("errorDescription", exception.errorDescription);
        return "board/error/request-failed";
    }

    @ExceptionHandler(NoCommentFoundException.class)
    public String noCommentFound(Model model, HttpServletResponse response, NoCommentFoundException exception) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("errorTitle", exception.errorTitle);
        model.addAttribute("errorDescription", exception.errorDescription);
        return "board/error/request-failed";
    }

    @ExceptionHandler(CommentAuthFailedException.class)
    public String commentAuthFailed(Model model, HttpServletResponse response, CommentAuthFailedException exception) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        model.addAttribute("errorTitle", exception.errorTitle);
        model.addAttribute("errorDescription", exception.errorDescription);
        return "board/error/request-failed";
    }
}
