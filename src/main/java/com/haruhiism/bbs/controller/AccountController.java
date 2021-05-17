package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.account.*;
import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.dto.*;
import com.haruhiism.bbs.exception.auth.AuthenticationFailedException;
import com.haruhiism.bbs.service.account.AccountService;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final ArticleService articleService;
    private final AccountService accountService;
    private final CommentService commentService;

    private final String sessionAuthAttribute = "loginSessionInfo";


    @GetMapping("/register")
    public String requestRegister(@ModelAttribute("command") RegisterRequestCommand command) {
        return "account/register";
    }

    @PostMapping("/register")
    public String submitRegister(HttpServletRequest request,
                                 HttpServletResponse response,
                                 @ModelAttribute("command") @Valid RegisterRequestCommand command,
                                 BindingResult bindingResult){
        if(bindingResult.hasErrors()) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
            return "account/register";
        }
        if(accountService.isDuplicatedUserID(command.getUserid())) {
            // when manually add field binding errors to binding result, use FieldError. Not ObjectError!
            bindingResult.addError(new FieldError("command", "userid", "Duplicated id."));
            return "account/register";
        }

        accountService.registerAccount(new BoardAccountDTO(command), AccountLevel.NORMAL);
        HttpSession session = request.getSession();
        session.setAttribute(sessionAuthAttribute, accountService.loginAccount(
                BoardAccountDTO.builder().userId(command.getUserid()).build(),
                AuthDTO.builder().rawPassword(command.getPassword()).build()));
        return "redirect:/board/list";
    }


    @GetMapping("/withdraw")
    public String requestWithdraw(){
        return "account/withdraw";
    }

    @PostMapping("/withdraw")
    public String submitWithdraw(HttpServletResponse response,
                                 @ModelAttribute("command") @Valid WithdrawRequestCommand command,
                                 HttpSession session,
                                 @SessionAttribute(name = "loginSessionInfo") LoginSessionInfo loginSessionInfo){
        try {
            accountService.withdrawAccount(
                    BoardAccountDTO.builder().userId(loginSessionInfo.getUserID()).build(),
                    AuthDTO.builder().rawPassword(command.getPassword()).build());
            session.invalidate();
        } catch (AuthenticationFailedException exception){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "account/withdraw";
        }

        return "redirect:/board/list";
    }


    @GetMapping("/login")
    public String requestLogin() {
        return "account/login";
    }

    @PostMapping("/login")
    public String submitLogin(@ModelAttribute(name = "command") @Valid LoginRequestCommand command,
                              HttpServletRequest request,
                              HttpServletResponse response){
        // session object is always given when HttpSession parameter is set. session object is either newly generated session or existing session.
        try {
            LoginSessionInfo loginResult = accountService.loginAccount(
                    BoardAccountDTO.builder().userId(command.getUserid()).build(),
                    AuthDTO.builder().rawPassword(command.getPassword()).build());
            HttpSession session = request.getSession();
            session.setAttribute(sessionAuthAttribute, loginResult);
        } catch (AuthenticationFailedException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "account/login";
        }

        return "redirect:/board/list";
    }


    @GetMapping("/logout")
    public String requestLogout(HttpSession session){
        session.invalidate();
        return "redirect:/board/list";
    }


    @GetMapping("/manage")
    public String manage(@RequestParam(name = "articlePage", required = false, defaultValue = "0") int articlePage,
                         @RequestParam(name = "commentPage", required = false, defaultValue = "0") int commentPage,
                         Model model,
                         @SessionAttribute(name = "loginSessionInfo") LoginSessionInfo loginSessionInfo) {

        BoardArticlesDTO boardArticles = articleService.readArticlesOfAccount(loginSessionInfo.getUserID(), articlePage, 10);
        BoardCommentsDTO boardComments = commentService.readCommentsOfAccount(loginSessionInfo.getUserID(), commentPage, 10);
        BoardAccountLevelDTO accountLevels = accountService.getAccountLevels(BoardAccountDTO.builder().userId(loginSessionInfo.getUserID()).build());

        model.addAttribute("userInfo", loginSessionInfo);
        model.addAttribute("articles", boardArticles.getBoardArticles());
        model.addAttribute("comments", boardComments.getBoardComments());

        model.addAttribute("currentArticlePage", boardArticles.getCurrentPage());
        model.addAttribute("articlePages", boardArticles.getTotalPages());
        model.addAttribute("currentCommentPage", boardComments.getCurrentPage());
        model.addAttribute("commentPages", boardComments.getTotalPages());

        model.addAttribute("levels", accountLevels.getLevels());

        return "account/info";
    }


    @GetMapping("/manage/change")
    public String requestChangePersonalInformation(@Valid InfoUpdateRequestCommand command,
                                                   @SessionAttribute("loginSessionInfo") LoginSessionInfo loginSessionInfo,
                                                   Model model) {
        model.addAttribute("attributeType", command.getMode().name());

        switch(command.getMode()){
            case password:
                model.addAttribute("attributeValue", "********");
                break;

            case email:
                model.addAttribute("attributeValue", loginSessionInfo.getEmail());
                break;

            case username:
                model.addAttribute("attributeValue", loginSessionInfo.getUsername());
                break;
        }

        return "account/change";
    }

    @PostMapping("/manage/change")
    public String submitChangePersonalInformation(HttpSession session,
                                                  HttpServletResponse response,
                                                  @Valid InfoUpdateSubmitCommand command,
                                                  @SessionAttribute(name = "loginSessionInfo") LoginSessionInfo loginSessionInfo){
        try {
            LoginSessionInfo updateResult = accountService.updateAccount(
                    new BoardAccountDTO(loginSessionInfo),
                    AuthDTO.builder().rawPassword(command.getAuth()).loginSessionInfo(loginSessionInfo).build(),
                    command.getMode(),
                    command.getUpdated());

            session.setAttribute(sessionAuthAttribute, updateResult);
        } catch (AuthenticationFailedException exception) {
            // TODO: send to password input view with selected update field and value when auth failed.
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "account/change";
        }
        return "redirect:/account/manage";
    }
}
