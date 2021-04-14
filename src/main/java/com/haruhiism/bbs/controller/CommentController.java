package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.comment.CommentRemoveRequestCommand;
import com.haruhiism.bbs.command.comment.CommentSubmitCommand;
import com.haruhiism.bbs.domain.dto.AuthDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentDTO;
import com.haruhiism.bbs.exception.AuthenticationFailedException;
import com.haruhiism.bbs.exception.CommentAuthFailedException;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    @PostMapping("/create")
    public String createComment(@Valid CommentSubmitCommand command,
                                HttpServletRequest request) {
        LoginSessionInfo loginSessionInfo = getLoginSessionInfoFromHttpSession(request.getSession(false));

        commentService.createComment(BoardCommentDTO.builder()
                .articleID(command.getArticleID())
                .writer(command.getWriter())
                .password(command.getPassword())
                .content(command.getContent()).build(),
                AuthDTO.builder().loginSessionInfo(loginSessionInfo).build());

        return String.format("redirect:/board/read?id=%d", command.getArticleID());
    }


    @GetMapping("/remove")
    public String requestRemoveComment(Model model,
                                       @RequestParam("id") Long commentID,
                                       HttpServletRequest request){

        // TODO: Article 쪽도 그냥 이렇게 하는걸 고려해보기.
        BoardCommentDTO commentDTO = commentService.readComment(commentID);
        if (commentDTO.isWrittenByAccount()) {
            LoginSessionInfo loginSessionInfo = getLoginSessionInfoFromHttpSession(request.getSession(false));
            if(loginSessionInfo == null) throw new AuthenticationFailedException();

            commentService.deleteComment(commentID, AuthDTO.builder().loginSessionInfo(loginSessionInfo).build());
            return String.format("redirect:/board/read?id=%d",commentDTO.getArticleID());
        } else {
            model.addAttribute("commentID", commentID);
            return "comment/removeRequest";
        }
    }

    @PostMapping("/remove")
    public String submitRemoveComment(@Valid CommentRemoveRequestCommand command) {
        BoardCommentDTO commentDTO = commentService.readComment(command.getCommentID());
        commentService.deleteComment(command.getCommentID(), AuthDTO.builder().rawPassword(command.getPassword()).build());
        return String.format("redirect:/board/read?id=%d", commentDTO.getArticleID());
    }

    private LoginSessionInfo getLoginSessionInfoFromHttpSession(HttpSession session) {
        if(session == null) return null;
        String sessionAuthAttribute = "loginSessionInfo";
        return (LoginSessionInfo) session.getAttribute(sessionAuthAttribute);
    }
}
