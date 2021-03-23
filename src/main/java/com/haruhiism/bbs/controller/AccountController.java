package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.account.LoginRequestCommand;
import com.haruhiism.bbs.command.account.WithdrawRequestCommand;
import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.command.account.RegisterRequestCommand;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.exception.AuthenticationFailedException;
import com.haruhiism.bbs.exception.NoAccountFoundException;
import com.haruhiism.bbs.service.account.AccountService;
import com.haruhiism.bbs.service.authentication.LoginSessionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    private final String sessionAuthAttribute = "loginAuthInfo";

    @GetMapping("/register")
    public String requestRegister(@ModelAttribute("command") RegisterRequestCommand command) {
        return "account/register";
    }

    @PostMapping("/register")
    public String submitRegister(@ModelAttribute("command") @Valid RegisterRequestCommand command, BindingResult bindingResult){
        if(bindingResult.hasErrors()) {
            return "account/register";
        }
        if(accountService.isDuplicatedAccountByID(command.getUserid())) {
            // when manually add field binding errors to binding result, use FieldError. Not ObjectError!
            bindingResult.addError(new FieldError("command", "userid", "Duplicated id."));
            return "account/register";
        }

        accountService.registerAccount(
                new BoardAccount(command.getUserid(), command.getUsername(), command.getPassword(), command.getEmail()),
                AccountLevel.NORMAL);

        return "redirect:/board/list";
    }


    @GetMapping("/withdraw")
    public String requestWithdraw(){
        return "account/withdraw";
    }

    @PostMapping("/withdraw")
    public String submitWithdraw(@ModelAttribute("command") @Valid WithdrawRequestCommand command, HttpSession session){

        try {
            BoardAccount account = accountService.authenticateAccount(
                    ((LoginSessionInfo) session.getAttribute(sessionAuthAttribute)).getUserID(),
                    command.getPassword());

            accountService.withdrawAccount(account);
            session.invalidate();
        } catch (NoAccountFoundException | AuthenticationFailedException exception){
            return "account/withdraw";
        }

        return "redirect:/board/list";
    }


    @GetMapping("/login")
    public String requestLogin(HttpServletRequest request) {
        return "account/login";
    }

    @PostMapping("/login")
    public String submitLogin(@ModelAttribute(name = "command") @Valid LoginRequestCommand command, BindingResult bindingResult, HttpServletRequest request /*HttpSession session*/){
        // session object is always given when HttpSession parameter is set. session object is either newly generated session or existing session.
        if(bindingResult.hasErrors()) return "/account/login";

        try {
            LoginSessionInfo loginSessionInfo = accountService.loginAccount(command.getUserid(), command.getPassword());
            HttpSession session = request.getSession();
            session.setAttribute(sessionAuthAttribute, loginSessionInfo);
        } catch (AuthenticationFailedException | NoAccountFoundException e){
            return "/account/login";
        }

        return "redirect:/board/list";
    }


    @GetMapping("/logout")
    public String requestLogout(HttpSession session){
        session.invalidate();
        return "redirect:/board/list";
    }


    @GetMapping("/manage")
    public String manage() {
        return "redirect:/not-implemented.html";
    }
}
