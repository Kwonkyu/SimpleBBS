package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.haruhiism.bbs.domain.dto.BoardCommentDTO.*;


@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    @PostMapping("/create")
    public String createComment(@Validated(Submit.Create.class) Submit command,
                                BindingResult bindingResult,
                                @CurrentSecurityContext SecurityContext context) {

        if (bindingResult.hasErrors()) {
            return "redirect:/board/read?id=" + command.getArticleId();
        }

        Submit dto = Submit.builder()
                .articleId(command.getArticleId())
                .writer(command.getWriter())
                .password(command.getPassword())
                .content(command.getContent()).build();

        if(context.getAuthentication() instanceof AnonymousAuthenticationToken) {
            commentService.createComment(dto);
        } else {
            commentService.createComment(dto, context.getAuthentication().getName());
        }

        return "redirect:/board/read?id=" + command.getArticleId();
    }


    @GetMapping("/remove")
    public String requestRemoveComment(@ModelAttribute("command") @Validated(Authorize.Request.class) Authorize command,
                                       BindingResult bindingResult,
                                       Principal principal){

        if (bindingResult.hasErrors()) return "redirect:/board/list";

        Read comment = commentService.readComment(command.getId());
        if(comment.isAnonymous()) return "comment/removeRequest";
        if(principal != null && comment.getUserId().equals(principal.getName())) {
            commentService.deleteComment(command.getId());
        }

        return "redirect:/board/read?id=" + comment.getArticleId();
    }

    @PostMapping("/remove")
    public String submitRemoveComment(@ModelAttribute("command") @Validated(Authorize.Submit.class) Authorize command,
                                      BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "comment/removeRequest";
        }

        if (commentService.authorizeCommentAccess(command.getId(), command.getPassword())) {
            long article = commentService.deleteComment(command.getId());
            return "redirect:/board/read?id=" + article;
        } else {
            bindingResult.addError(new FieldError("command", "password", "Password not matched."));
            return "comment/removeRequest";
        }
    }
}
