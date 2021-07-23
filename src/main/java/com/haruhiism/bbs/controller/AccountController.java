package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.account.*;
import com.haruhiism.bbs.domain.ArticleSearchMode;
import com.haruhiism.bbs.domain.ManagerLevel;
import com.haruhiism.bbs.domain.UpdatableInformation;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentDTO;
import com.haruhiism.bbs.exception.account.NoAccountFoundException;
import com.haruhiism.bbs.service.account.AccountRecoveryService;
import com.haruhiism.bbs.service.account.AccountService;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final ArticleService articleService;
    private final AccountService accountService;
    private final AccountRecoveryService accountRecoveryService;
    private final CommentService commentService;


    @GetMapping("/register")
    public String requestRegister(@ModelAttribute("command") RegisterRequestCommand command) {
        return "account/register";
    }

    @PostMapping("/register")
    public String submitRegister(@ModelAttribute("command") @Valid RegisterRequestCommand command,
                                 BindingResult bindingResult){
        if(bindingResult.hasErrors()) {
            return "account/register";
        }

        if(accountService.isDuplicatedUserID(command.getUserId())) {
            // when manually add field binding errors to binding result, use FieldError. Not ObjectError!
            bindingResult.addError(new FieldError("command", "userid", "Duplicated id."));
            return "account/register";
        }

        accountService.registerAccount(new BoardAccountDTO(command));
        return "redirect:/board/list";
    }


    @GetMapping("/withdraw")
    public String requestWithdraw(@ModelAttribute("command") WithdrawRequestCommand command){
        return "account/withdraw";
    }

    @PostMapping("/withdraw")
    public String submitWithdraw(@ModelAttribute("command") @Valid WithdrawRequestCommand command,
                                 BindingResult bindingResult,
                                 @CurrentSecurityContext SecurityContext context,
                                 Principal principal){
        if(bindingResult.hasErrors()){
            return "account/withdraw";
        }

        if(accountService.authenticateAccount(principal.getName(), command.getPassword())) {
            accountService.withdrawAccount(principal.getName());
            context.setAuthentication(null);
        } else {
            bindingResult.addError(new FieldError("command", "password", "Password not matched."));
            return "account/withdraw";
        }

        return "redirect:/board/list";
    }


    @GetMapping("/login")
    public String requestLogin(@ModelAttribute(name = "command") LoginRequestCommand command,
                               BindingResult bindingResult,
                               @RequestParam(name = "failed", required = false) Object failed,
                               @RequestParam(name = "locked", required = false) Object locked,
                               Principal principal) {
        if(principal != null) return "redirect:/board/list";

        if(failed != null) bindingResult.addError(new FieldError("command", "userId", "ID or password not matched."));
        if(locked != null) bindingResult.addError(new FieldError("command", "userId", "Account login attempt exceeded its limit. Try 1 hour later."));

        return "account/login";
    }

    @GetMapping("/logout")
    public String requestLogout(Principal principal) {
        if(principal == null) return "redirect:/board/list";
        else return "account/logout";
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
            if (!accountRecoveryService.challengeAccount(command.getUserId())) {
                bindingResult.addError(new FieldError("command", "userId", "Recovery challenge limit exceeded. Try 1 hour later."));
                return "account/recoveryRequest";
            }
        } catch (NoAccountFoundException exception) {
            bindingResult.addError(new FieldError("command", "userId", "No account found."));
            return "account/recoveryRequest";
        }

        model.addAttribute("question", accountService.getAccountInformation(command.getUserId()).getRecoveryQuestion());
        return "account/recovery";
    }
    
    @PostMapping("/recovery/submit")
    public String submitRestoreAccount(@ModelAttribute("command")
                                       @Validated(AccountRecoverySubmitValidationGroup.class) AccountRecoveryCommand command,
                                       BindingResult bindingResult,
                                       Model model) {

        if(bindingResult.hasErrors()){
            return "account/recovery";
        }
        BoardAccountDTO account = accountService.getAccountInformation(command.getUserId());
        if(!accountRecoveryService.getChallengeStatus(command.getUserId())) {
            model.addAttribute("question", account.getRecoveryQuestion());
            bindingResult.addError(new FieldError("command", "answer", "Recovery challenge limit exceeded."));
            return "account/recovery";
        }

        if (!accountRecoveryService.recoverAccount(command.getUserId(), command.getAnswer())) {
            model.addAttribute("question", account.getRecoveryQuestion());
            bindingResult.addError(new FieldError("command", "answer", "Recovery answer not matched."));
            return "account/recovery";
        } else {
            accountService.updateAccount(command.getUserId(), UpdatableInformation.password, command.getNewPassword());
            return "redirect:/account/login";
        }
    }


    @GetMapping("/manage")
    public String manage(@Valid AccountManageListCommand command,
                         BindingResult bindingResult,
                         Model model,
                         Principal principal) {

        if (bindingResult.hasErrors()) {
            return "redirect:/account/manage";
        }

        BoardArticleDTO.PagedArticles articles = articleService.searchAllByPages(ArticleSearchMode.ACCOUNT, principal.getName(), command.getArticlePage(), 10);
        BoardCommentDTO.PagedComments comments = commentService.readCommentsOfAccount(principal.getName(), command.getCommentPage(), 10);
        BoardAccountDTO account = accountService.getAccountInformation(principal.getName());
        List<ManagerLevel> accountLevels = accountService.getAccountManagerAuthorities(principal.getName());

        model.addAttribute("userInfo", account);
        model.addAttribute("articles", articles.getArticles());
        model.addAttribute("comments", comments.getComments());

        model.addAttribute("currentArticlePage", articles.getCurrentPage());
        model.addAttribute("articlePages", articles.getPages());
        model.addAttribute("currentCommentPage", comments.getCurrentPage());
        model.addAttribute("commentPages", comments.getPages());

        model.addAttribute("levels", accountLevels);

        return "account/info";
    }


    @GetMapping("/manage/change")
    public String requestChangePersonalInformation(@ModelAttribute("command")
                                                   @Validated(InfoUpdateRequestValidationGroup.class)
                                                           InfoUpdateRequestCommand command,
                                                   BindingResult bindingResult,
                                                   Principal principal,
                                                   Model model) {

        if (bindingResult.hasErrors()) {
            return "redirect:/account/manage";
        }

        BoardAccountDTO boardAccountDTO = accountService.getAccountInformation(principal.getName());

        switch (command.getMode()) {
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
    public String submitChangePersonalInformation(Model model,
                                                  @ModelAttribute("command")
                                                  @Validated(InfoUpdateSubmitValidationGroup.class)
                                                          InfoUpdateRequestCommand command,
                                                  BindingResult bindingResult,
                                                  Principal principal){
        if (bindingResult.hasErrors()) {
            model.addAttribute("previousValue", command.getPrevious());
            return "account/change";
        }

        if(!accountService.authenticateAccount(principal.getName(), command.getAuth())) {
            model.addAttribute("previousValue", command.getPrevious());
            bindingResult.addError(new FieldError("command", "auth", "Authentication string not matched."));
            return "account/change";
        }

        accountService.updateAccount(principal.getName(), command.getMode(), command.getUpdated());
        return "redirect:/account/manage";
    }
}
