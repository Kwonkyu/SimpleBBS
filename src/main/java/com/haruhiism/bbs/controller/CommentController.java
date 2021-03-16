package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.domain.command.comment.CommentSubmitCommand;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import com.haruhiism.bbs.service.comment.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;


    @PostMapping("/create")
    // order of parameter matters!!
    public String createComment(@Valid CommentSubmitCommand command, BindingResult bindingResult, HttpServletResponse response) {
        if(bindingResult.hasErrors()){
            if(command.getArticleID() == null){
                return "redirect:/board/list";
            } else {
                return String.format("redirect:/board/read?id=%d", command.getArticleID());
            }
        }

        commentService.createComment(new BoardComment(
                command.getWriter(),
                command.getPassword(),
                command.getContent(),
                command.getArticleID()));
        return String.format("redirect:/board/read?id=%d", command.getArticleID());
    }

}
