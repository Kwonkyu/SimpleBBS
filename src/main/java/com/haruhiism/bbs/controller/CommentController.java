package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.comment.CommentRemoveRequestCommand;
import com.haruhiism.bbs.command.comment.CommentRemoveRequestValidationGroup;
import com.haruhiism.bbs.command.comment.CommentRemoveSubmitValidationGroup;
import com.haruhiism.bbs.command.comment.CommentSubmitCommand;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.dto.AuthDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentDTO;
import com.haruhiism.bbs.exception.auth.AuthenticationFailedException;
import com.haruhiism.bbs.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    @PostMapping("/create")
    public String createComment(@Valid CommentSubmitCommand command,
                                BindingResult bindingResult,
                                HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "redirect:/board/read?id=" + command.getArticleID();
        }

        LoginSessionInfo loginSessionInfo = getLoginSessionInfoFromHttpSession(request.getSession(false));

        commentService.createComment(BoardCommentDTO.builder()
                .articleId(command.getArticleID())
                .writer(command.getWriter())
                .password(command.getPassword())
                .content(command.getContent()).build(),
                AuthDTO.builder().loginSessionInfo(loginSessionInfo).build());

        return String.format("redirect:/board/read?id=%d", command.getArticleID());
    }


    @GetMapping("/remove")
    public String requestRemoveComment(@ModelAttribute("command") @Validated(CommentRemoveRequestValidationGroup.class) CommentRemoveRequestCommand command,
                                       BindingResult bindingResult,
                                       HttpServletRequest request){

        if (bindingResult.hasErrors()) {
            return "redirect:/board/read?id=" + command.getId();
        }

        BoardCommentDTO commentDTO = commentService.readComment(command.getId());
        if (!commentDTO.getUserId().isBlank()) {
            LoginSessionInfo loginSessionInfo = getLoginSessionInfoFromHttpSession(request.getSession(false));
            if(loginSessionInfo != null) {
                commentService.deleteComment(command.getId(), AuthDTO.builder().loginSessionInfo(loginSessionInfo).build());
            }

            return "redirect:/board/read?id=" + commentDTO.getArticleId();
        } else {
            return "comment/removeRequest";
        }
    }

    @PostMapping("/remove")
    public String submitRemoveComment(@ModelAttribute("command") @Validated(CommentRemoveSubmitValidationGroup.class) CommentRemoveRequestCommand command,
                                      BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return "comment/removeRequest";
        }

        try {
            BoardCommentDTO commentDTO = commentService.readComment(command.getId());
            commentService.deleteComment(command.getId(), AuthDTO.builder().rawPassword(command.getPassword()).build());
            return "redirect:/board/read?id=" + commentDTO.getArticleId();
        } catch (AuthenticationFailedException exception) {
            bindingResult.addError(new FieldError("command", "password", "Password not matched."));
            return "comment/removeRequest";
        }
    }

    private LoginSessionInfo getLoginSessionInfoFromHttpSession(HttpSession session) {
        if(session == null) return null;
        return (LoginSessionInfo) session.getAttribute("loginSessionInfo");
    }
}
