package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.account.*;
import com.haruhiism.bbs.domain.UpdatableInformation;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.dto.*;
import com.haruhiism.bbs.exception.account.AccountChallengeThresholdLimitExceededException;
import com.haruhiism.bbs.exception.account.NoAccountFoundException;
import com.haruhiism.bbs.exception.auth.AuthenticationFailedException;
import com.haruhiism.bbs.service.account.AccountService;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
                                 @ModelAttribute("command") @Valid RegisterRequestCommand command,
                                 BindingResult bindingResult){

        if(bindingResult.hasErrors()) {
            return "account/register";
        }

        if(accountService.isDuplicatedUserID(command.getUserid())) {
            // when manually add field binding errors to binding result, use FieldError. Not ObjectError!
            bindingResult.addError(new FieldError("command", "userid", "Duplicated id."));
            return "account/register";
        }

        accountService.registerAccount(new BoardAccountDTO(command));
        HttpSession session = request.getSession();
        session.setAttribute(sessionAuthAttribute, accountService.loginAccount(
                BoardAccountDTO.builder().userId(command.getUserid()).build(),
                AuthDTO.builder().rawPassword(command.getPassword()).build()));

        return "redirect:/board/list";
    }


    @GetMapping("/withdraw")
    public String requestWithdraw(@ModelAttribute("command") WithdrawRequestCommand command){
        return "account/withdraw";
    }

    @PostMapping("/withdraw")
    public String submitWithdraw(@ModelAttribute("command") @Valid WithdrawRequestCommand command,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 @SessionAttribute(name = "loginSessionInfo") LoginSessionInfo loginSessionInfo){

        if(bindingResult.hasErrors()){
            return "account/withdraw";
        }

        try {
            accountService.withdrawAccount(
                    BoardAccountDTO.builder().userId(loginSessionInfo.getUserID()).build(),
                    AuthDTO.builder().rawPassword(command.getPassword()).build());
            session.invalidate();
        } catch (AuthenticationFailedException exception){
            bindingResult.addError(new FieldError("command", "password", "Password not matched."));
            return "account/withdraw";
        }

        return "redirect:/board/list";
    }


    @GetMapping("/login")
    public String requestLogin(@ModelAttribute(name = "command") LoginRequestCommand command) {
        return "account/login";
    }

    @PostMapping("/login")
    public String submitLogin(@ModelAttribute(name = "command") @Valid LoginRequestCommand command,
                              BindingResult bindingResult,
                              HttpServletRequest request){

        if(bindingResult.hasErrors()){
            return "account/login";
        }

        try {
            LoginSessionInfo loginResult = accountService.loginAccount(
                    BoardAccountDTO.builder().userId(command.getUserid()).build(),
                    AuthDTO.builder().rawPassword(command.getPassword()).build());
            HttpSession session = request.getSession();
            session.setAttribute(sessionAuthAttribute, loginResult);
        } catch (NoAccountFoundException e){
            bindingResult.addError(new FieldError("command", "userid", e.errorDescription));
            return "account/login";
        } catch (AuthenticationFailedException e) {
            bindingResult.addError(new FieldError("command", "userid", "Id or Password is not matched."));
            return "account/login";
        } catch (AccountChallengeThresholdLimitExceededException e){
            bindingResult.addError(new FieldError("command", "userid", "Login attempt limit exceed. Please try again later."));
            return "account/login";
        }

        return "redirect:/board/list";
    }


    @GetMapping("/logout")
    public String requestLogout(HttpSession session){
        session.invalidate();
        return "redirect:/board/list";
    }


    @GetMapping("/recovery")
    public String requestRecovery(@ModelAttribute("command") AccountRecoveryCommand command){
        return "account/recoveryRequest";
    }

    @PostMapping("/recovery")
    public String recoverAccount(@ModelAttribute("command")
                                 @Validated(AccountRecoveryRequestValidationGroup.class) AccountRecoveryCommand command,
                                 BindingResult bindingResult,
                                 Model model) {

        if (bindingResult.hasErrors()) {
            return "account/recoveryRequest";
        }

        try {
            BoardAccountDTO boardAccountDTO = accountService.readAccount(BoardAccountDTO.builder().userId(command.getUserId()).build());
            model.addAttribute("question", boardAccountDTO.getRecoveryQuestion());
            return "account/recovery";
        } catch (NoAccountFoundException exception) {
            bindingResult.addError(new FieldError("command", "userId", "No account found."));
            return "account/recoveryRequest";
        }
    }
    
    @PostMapping("/recovery/submit")
    public String submitRestoreAccount(Model model,
                                       @ModelAttribute("command")
                                       @Validated(AccountRecoverySubmitValidationGroup.class) AccountRecoveryCommand command,
                                       BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return "account/recovery";
        }

        BoardAccountDTO request = BoardAccountDTO.builder().userId(command.getUserId()).build();
        try {
            accountService.updateAccount(
                    request,
                    AuthDTO.builder().recoveryAnswer(command.getAnswer()).build(),
                    UpdatableInformation.password,
                    command.getNewPassword());
            return "redirect:/account/login";
        } catch (AuthenticationFailedException exception){
            model.addAttribute("question", command.getQuestion());
            bindingResult.addError(new FieldError("command", "answer", "Recovery answer not matched."));
            return "account/recovery";
        } catch (AccountChallengeThresholdLimitExceededException exception){
            model.addAttribute("question", command.getQuestion());
            bindingResult.addError(new FieldError("command", "newPassword", "Login attempt threshold limit exceeded."));
            return "account/recovery";
        }
    }


    @GetMapping("/manage")
    public String manage(@Valid AccountManageListCommand command,
                         BindingResult bindingResult,
                         Model model,
                         @SessionAttribute(name = "loginSessionInfo") LoginSessionInfo loginSessionInfo) {

        if (bindingResult.hasErrors()) {
            return "redirect:/account/manage";
        }

        BoardArticlesDTO boardArticles = articleService.readArticlesOfAccount(loginSessionInfo.getUserID(), command.getArticlePage(), 10);
        BoardCommentsDTO boardComments = commentService.readCommentsOfAccount(loginSessionInfo.getUserID(), command.getCommentPage(), 10);
        BoardAccountDTO boardAccountDTO = accountService.readAccount(BoardAccountDTO.builder().userId(loginSessionInfo.getUserID()).build());
        BoardAccountLevelDTO accountLevels = accountService.getAccountLevels(BoardAccountDTO.builder().userId(loginSessionInfo.getUserID()).build());

        model.addAttribute("userInfo", boardAccountDTO);
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
    public String requestChangePersonalInformation(@ModelAttribute("command")
                                                   @Validated(InfoUpdateRequestValidationGroup.class)
                                                   InfoUpdateRequestCommand command,
                                                   BindingResult bindingResult,
                                                   @SessionAttribute("loginSessionInfo") LoginSessionInfo loginSessionInfo,
                                                   Model model) {

        if(bindingResult.hasErrors()){
            return "redirect:/account/manage";
        }

        BoardAccountDTO boardAccountDTO = accountService.readAccount(BoardAccountDTO.builder().userId(loginSessionInfo.getUserID()).build());

        switch(command.getMode()){
            case password:
                model.addAttribute("previousValue", "********");
                break;

            case email:
                model.addAttribute("previousValue", boardAccountDTO.getEmail());
                break;

            case username:
                model.addAttribute("previousValue", boardAccountDTO.getUsername());
                break;

            case question:
                model.addAttribute("previousValue", boardAccountDTO.getRecoveryQuestion());
                break;

            case answer:
                model.addAttribute("previousValue", boardAccountDTO.getRecoveryAnswer());
                break;
        }

        return "account/change";
    }

    @PostMapping("/manage/change")
    public String submitChangePersonalInformation(HttpSession session,
                                                  Model model,
                                                  @ModelAttribute("command")
                                                  @Validated(InfoUpdateSubmitValidationGroup.class)
                                                  InfoUpdateRequestCommand command,
                                                  BindingResult bindingResult,
                                                  @SessionAttribute(name = "loginSessionInfo") LoginSessionInfo loginSessionInfo){

        if (bindingResult.hasErrors()) {
            model.addAttribute("previousValue", command.getPrevious());
            return "account/change";
        }

        try {
            LoginSessionInfo updateResult = accountService.updateAccount(
                    new BoardAccountDTO(loginSessionInfo),
                    AuthDTO.builder().rawPassword(command.getAuth()).build(),
                    command.getMode(),
                    command.getUpdated());

            session.setAttribute(sessionAuthAttribute, updateResult);
        } catch (AuthenticationFailedException exception) {
            model.addAttribute("previousValue", command.getPrevious());
            bindingResult.addError(new FieldError("command", "auth", "Authentication string not matched."));
            return "account/change";
        }

        return "redirect:/account/manage";
    }
}
