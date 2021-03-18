package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.domain.command.account.RegisterRequestCommand;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/register")
    public String requestRegister(@ModelAttribute("command") RegisterRequestCommand command) {
        return "account/register";
    }

    @PostMapping("/register")
    // TODO: validation을 적용했을 때 user feedback이 꼭 필요한가?
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


    @GetMapping("/login")
    public String requestLogin() {
        return "redirect:/not-implemented.html";
    }

    @PostMapping("/login")
    public String submitLogin(){
        return "redirect:/not-implemented.html";
    }


    @GetMapping("/manage")
    public String manage() {
        return "redirect:/not-implemented.html";
    }
}
