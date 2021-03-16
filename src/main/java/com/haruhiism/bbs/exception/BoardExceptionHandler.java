package com.haruhiism.bbs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class BoardExceptionHandler {

    @ExceptionHandler(NoArticleFoundException.class)
    public String noArticleFound(Model model, HttpServletResponse response){
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("errorTitle", "Requested Article Not Found.");
        model.addAttribute("errorDescription", "Requested Article is deleted or does not exist.");
        return "board/error/request-failed";
    }

    @ExceptionHandler(ArticleAuthFailedException.class)
    public String articleEditAuthFailed(Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        model.addAttribute("errorTitle", "Requested Operation Not Permitted.");
        model.addAttribute("errorDescription", "You don't have privilege to edit this article.");
        return "board/error/request-failed";
    }

    @ExceptionHandler(UpdateDeletedArticleException.class)
    public String updateDeletedArticle(Model model, HttpServletResponse response){
        response.setStatus(HttpStatus.GONE.value());
        model.addAttribute("errorTitle", "Update Article Not Found.");
        model.addAttribute("errorDescription", "Article to update is not found. Maybe article is already deleted.");
        return "board/error/request-failed";
    }

    @ExceptionHandler(NoCommentFoundException.class)
    public String noCommentFound(Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("errorTitle", "Request Comment Not Found.");
        model.addAttribute("errorDescription", "Comment to remove is not found. Maybe comment is already deleted.");
        return "board/error/request-failed";
    }

    @ExceptionHandler(CommentAuthFailedException.class)
    public String commentAuthFailed(Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        model.addAttribute("errorTitle", "Requested Operation Not Permitted.");
        model.addAttribute("errorDescription", "You don't have privilege to edit this comment.");
        return "board/error/request-failed";
    }
}
