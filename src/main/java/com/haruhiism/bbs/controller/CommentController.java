package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.comment.CommentRemoveRequestCommand;
import com.haruhiism.bbs.command.comment.CommentRemoveRequestValidationGroup;
import com.haruhiism.bbs.command.comment.CommentRemoveSubmitValidationGroup;
import com.haruhiism.bbs.command.comment.CommentSubmitCommand;
import com.haruhiism.bbs.domain.dto.BoardCommentDTO;
import com.haruhiism.bbs.exception.auth.AuthenticationFailedException;
import com.haruhiism.bbs.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    @PostMapping("/create")
    public String createComment(@Valid CommentSubmitCommand command,
                                BindingResult bindingResult,
                                @CurrentSecurityContext SecurityContext context) {

        if (bindingResult.hasErrors()) {
            return "redirect:/board/read?id=" + command.getArticleId();
        }

        BoardCommentDTO dto = new BoardCommentDTO(command);
        // test usage of security context.
        if(context.getAuthentication() instanceof AnonymousAuthenticationToken) {
            commentService.createComment(dto);
        } else {
            commentService.createComment(dto, context.getAuthentication().getName());
        }

        return "redirect:/board/read?id=" + command.getArticleId();
    }


    @GetMapping("/remove")
    public String requestRemoveComment(@ModelAttribute("command") @Validated(CommentRemoveRequestValidationGroup.class)
                                       CommentRemoveRequestCommand command,
                                       BindingResult bindingResult,
                                       Principal principal){

        if (bindingResult.hasErrors()) {
            return "redirect:/board/read?id=" + command.getId();
        }

        BoardCommentDTO comment = commentService.readComment(command.getId());
        if(comment.isWrittenByAccount()) {
            if(principal == null || !principal.getName().equals(comment.getUserId()))
                throw new AuthenticationFailedException();
            else {
                commentService.deleteComment(command.getId());
                return "redirect:/board/read?id=" + comment.getArticleId();
            }
        } else {
            return "comment/removeRequest";
        }
    }

    @PostMapping("/remove")
    public String submitRemoveComment(@ModelAttribute("command") @Validated(CommentRemoveSubmitValidationGroup.class)
                                      CommentRemoveRequestCommand command,
                                      BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return "comment/removeRequest";
        }

        BoardCommentDTO comment = commentService.readComment(command.getId());
        if(commentService.authorizeCommentAccess(command.getId(), command.getPassword())) {
            commentService.deleteComment(command.getId());
            return "redirect:/board/read?id=" + comment.getArticleId();
        } else {
            bindingResult.addError(new FieldError("command", "password", "Password not matched."));
            return "comment/removeRequest";
        }
    }
}
