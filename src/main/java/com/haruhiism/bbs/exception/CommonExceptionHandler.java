package com.haruhiism.bbs.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public String maxUploadSizeExceeded(Model model) {
        model.addAttribute("errorTitle", "Uploaded Resource Size Exceeds Limit.");
        model.addAttribute("errorDescription", "Uploaded resource's size exceeds limit. Please decrease request size.");
        return "error/request-failed";
    }
}
