package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.account.*;
import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.exception.account.NoAccountFoundException;
import com.haruhiism.bbs.exception.auth.AuthenticationFailedException;
import com.haruhiism.bbs.service.account.AccountService;
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

    private final AccountService accountService;

    private final String sessionAuthAttribute = "loginSessionInfo";

    @GetMapping("/register")
    public String requestRegister(@ModelAttribute("command") RegisterRequestCommand command) {
        return "account/register";
    }

    @PostMapping("/register")
    public String submitRegister(HttpServletResponse response, @ModelAttribute("command") @Valid RegisterRequestCommand command, BindingResult bindingResult){
        if(bindingResult.hasErrors()) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
            return "account/register";
        }
        if(accountService.isDuplicatedAccountByID(command.getUserid())) {
            // when manually add field binding errors to binding result, use FieldError. Not ObjectError!
            bindingResult.addError(new FieldError("command", "userid", "Duplicated id."));
            return "account/register";
        }

        accountService.registerAccount(
                new BoardAccountDTO(
                        command.getUserid(),
                        command.getUsername(),
                        command.getPassword(),
                        command.getEmail()),
                AccountLevel.NORMAL);

        return "redirect:/board/list";
    }


    @GetMapping("/withdraw")
    public String requestWithdraw(){
        return "account/withdraw";
    }

    @PostMapping("/withdraw")
    public String submitWithdraw(HttpServletResponse response, @ModelAttribute("command") @Valid WithdrawRequestCommand command, HttpSession session){

        try {
            BoardAccount account = accountService.authenticateAccount(
                    ((LoginSessionInfo) session.getAttribute(sessionAuthAttribute)).getUserID(),
                    command.getPassword());

            accountService.withdrawAccount(account);
            session.invalidate();
        } catch (NoAccountFoundException | AuthenticationFailedException exception){
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
    public String submitLogin(@ModelAttribute(name = "command") @Valid LoginRequestCommand command, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response /*HttpSession session*/){
        // session object is always given when HttpSession parameter is set. session object is either newly generated session or existing session.
        if(bindingResult.hasErrors()) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
            return "/account/login";
        }

        try {
            LoginSessionInfo loginSessionInfo = accountService.loginAccount(command.getUserid(), command.getPassword());
            HttpSession session = request.getSession();
            session.setAttribute(sessionAuthAttribute, loginSessionInfo);
        } catch (AuthenticationFailedException | NoAccountFoundException e){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
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
    public String manage(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page, Model model, HttpSession session) {
        LoginSessionInfo loginSessionInfo = (LoginSessionInfo) session.getAttribute(sessionAuthAttribute);
        BoardArticlesDTO boardArticles = accountService.readArticlesOfAccount(loginSessionInfo.getUserID(), page);

        model.addAttribute("userInfo", loginSessionInfo);
        model.addAttribute("pageCount", boardArticles.getTotalPages());
        model.addAttribute("articles", boardArticles.getBoardArticles());

        int[] pages = new int[boardArticles.getTotalPages()];
        for(int i=0;i<pages.length;i++){
            pages[i] = i;
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("pages", pages);

        return "account/info";
    }


    @GetMapping("/manage/change")
    public String requestChangePersonalInformation(HttpServletRequest request, Model model, @Valid InfoUpdateRequestCommand command) {
        model.addAttribute("attributeType", command.getMode().name());
        LoginSessionInfo userInfo = (LoginSessionInfo) request.getSession(false).getAttribute(sessionAuthAttribute);

        switch(command.getMode()){
            case password:
                model.addAttribute("attributeValue", "********");
                break;

            case email:
                model.addAttribute("attributeValue", userInfo.getEmail());
                break;

            case username:
                model.addAttribute("attributeValue", userInfo.getUsername());
                break;
        }

        return "account/change";
    }

    @PostMapping("/manage/change")
    public String submitChangePersonalInformation(HttpServletRequest request, HttpServletResponse response, @Valid InfoUpdateSubmitCommand command, BindingResult result){
        if(result.hasErrors()){
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
            return "account/change";
        }

        HttpSession session = request.getSession(false);
        LoginSessionInfo userInfo = (LoginSessionInfo) session.getAttribute(sessionAuthAttribute);
        LoginSessionInfo updatedUserInfo = accountService.updateAccount(userInfo.getUserID(), command.getAuth(), command.getMode(), command.getUpdated());
        session.setAttribute(sessionAuthAttribute, updatedUserInfo);
        return "redirect:/account/manage";
    }



}
