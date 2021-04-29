package com.haruhiism.bbs.exception;

import com.haruhiism.bbs.exception.article.NoArticleFoundException;
import com.haruhiism.bbs.exception.article.UpdateDeletedArticleException;
import com.haruhiism.bbs.exception.comment.NoCommentFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class BoardExceptionHandler {

    @ExceptionHandler(NoArticleFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noArticleFound(Model model, NoArticleFoundException exception){
        model.addAttribute("errorTitle", exception.errorTitle);
        model.addAttribute("errorDescription", exception.errorDescription);
        return "error/request-failed";
    }


    @ExceptionHandler(UpdateDeletedArticleException.class)
    @ResponseStatus(HttpStatus.GONE)
    public String updateDeletedArticle(Model model, UpdateDeletedArticleException exception){
        model.addAttribute("errorTitle", exception.errorTitle);
        model.addAttribute("errorDescription", exception.errorDescription);
        return "error/request-failed";
    }

    @ExceptionHandler(NoCommentFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noCommentFound(Model model, NoCommentFoundException exception) {
        model.addAttribute("errorTitle", exception.errorTitle);
        model.addAttribute("errorDescription", exception.errorDescription);
        return "error/request-failed";
    }
}
