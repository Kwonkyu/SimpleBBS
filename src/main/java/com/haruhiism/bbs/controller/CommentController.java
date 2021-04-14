package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.comment.CommentRemoveRequestCommand;
import com.haruhiism.bbs.command.comment.CommentSubmitCommand;
import com.haruhiism.bbs.domain.dto.BoardCommentDTO;
import com.haruhiism.bbs.exception.CommentAuthFailedException;
import com.haruhiism.bbs.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    @PostMapping("/create")
    public String createComment(@Valid CommentSubmitCommand command) {
        // TODO: handle login-ed user's request.
        commentService.createComment(
                new BoardCommentDTO(
                        command.getArticleID(),
                        command.getWriter(),
                        command.getPassword(),
                        command.getContent()));

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
            BoardCommentDTO boardCommentDTO = commentService.readComment(command.getCommentID());
            commentService.deleteComment(command.getCommentID());
            return String.format("redirect:/board/read?id=%d", boardCommentDTO.getArticleID());
        } else {
            throw new CommentAuthFailedException();
        }
    }
}
