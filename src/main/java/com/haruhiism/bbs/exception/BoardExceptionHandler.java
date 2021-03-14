package com.haruhiism.bbs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String methodArgumentTypeMismatch(Model model, HttpServletResponse response){
        // https://www.quora.com/Which-HTTP-code-is-best-suited-for-validation-errors-400-or-422
        response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        model.addAttribute("errorTitle", "Invalid Parameter Type");
        model.addAttribute("errorDescription", "Given parameter has un-processable entity. Check your request.");
        return "board/error/request-failed";
        // return "redirect:/not-available.html";
    }

    @ExceptionHandler(UpdateDeletedArticleException.class)
    public String updateDeletedArticle(Model model, HttpServletResponse response){
        response.setStatus(HttpStatus.GONE.value());
        model.addAttribute("errorTitle", "Update Article Not Found.");
        model.addAttribute("errordescription", "Article to update is not found. Maybe article is already deleted.");
        return "board/error/request-failed";
    }
}
