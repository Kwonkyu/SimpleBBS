package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.domain.command.comment.CommentRemoveRequestCommand;
import com.haruhiism.bbs.domain.command.comment.CommentSubmitCommand;
import com.haruhiism.bbs.domain.entity.BoardComment;
import com.haruhiism.bbs.exception.CommentAuthFailedException;
import com.haruhiism.bbs.service.comment.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;


    @PostMapping("/create")
    // order of parameter matters!!
    public String createComment(@Valid CommentSubmitCommand command) {
        commentService.createComment(new BoardComment(
                command.getWriter(),
                command.getPassword(),
                command.getContent(),
                command.getArticleID()));
        return String.format("redirect:/board/read?id=%d", command.getArticleID());
    }

    @GetMapping("/remove")
    public String requestRemoveComment(Model model, @RequestParam("id") Long commentID){
        model.addAttribute("commentID", commentID);
        return "comment/removeRequest";
    }

    @PostMapping("/remove")
    public String submitRemoveComment(@Valid CommentRemoveRequestCommand command) {
        if(commentService.authCommentAccess(command.getCommentID(), command.getPassword())) {
            Long commentedArticleID = commentService.readComment(command.getCommentID()).getArticleID();
            commentService.deleteComment(command.getCommentID());
            return String.format("redirect:/board/read?id=%d", commentedArticleID);
        } else {
            throw new CommentAuthFailedException();
        }
    }
}
