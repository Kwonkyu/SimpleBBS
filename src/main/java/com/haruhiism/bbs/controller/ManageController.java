package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.account.AccountListCommand;
import com.haruhiism.bbs.command.article.ArticleListCommand;
import com.haruhiism.bbs.command.comment.CommentListCommand;
import com.haruhiism.bbs.command.manage.AccountManagementCommand;
import com.haruhiism.bbs.command.manage.BoardManagementCommand;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.domain.dto.BoardAccountsDTO;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentsDTO;
import com.haruhiism.bbs.service.manage.AccountManagerService;
import com.haruhiism.bbs.service.manage.ArticleManagerService;
import com.haruhiism.bbs.service.manage.CommentManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/manage")
@RequiredArgsConstructor
public class ManageController {

    private final ArticleManagerService articleManagerService;
    private final CommentManagerService commentManagerService;
    private final AccountManagerService accountManagerService;

    private final String sessionAuthAttribute = "loginSessionInfo";

    @GetMapping("/console")
    public String showManagementPage(Model model,
                                     @SessionAttribute(sessionAuthAttribute)LoginSessionInfo loginSessionInfo){
        model.addAttribute("writtenArticles", articleManagerService.countAllArticles());
        model.addAttribute("deletedArticles", articleManagerService.countAllDeletedArticles());

        model.addAttribute("writtenComments", commentManagerService.countAllComments());
        model.addAttribute("deletedComments", commentManagerService.countAllDeletedComments());

        model.addAttribute("signedAccounts", accountManagerService.countAllAccounts());

        model.addAttribute("loginSessionInfo", loginSessionInfo);
        model.addAttribute("levels", accountManagerService.getLevelOfAccount(
                BoardAccountDTO.builder().userId(loginSessionInfo.getUserID()).build()));

        return "admin/management-console";
    }


    @GetMapping("/console/article")
    public String articleManagementPage(@Valid ArticleListCommand command, Model model){
        BoardArticlesDTO result;
        if(command.getKeyword().isBlank()) {
            result = articleManagerService.readArticles(command.getPageNum(), command.getPageSize());
        } else {
            result = articleManagerService.searchArticlesByPages(command.getMode(), command.getKeyword(), command.getPageNum(), command.getPageSize());
        }

        model.addAttribute("currentPage", result.getCurrentPage());
        model.addAttribute("pageSize", command.getPageSize());
        model.addAttribute("totalPage", result.getTotalPages());

        model.addAttribute("articles", result.getBoardArticles());
        model.addAttribute("commentSizes", result.getBoardArticleCommentSize());

        model.addAttribute("keyword", command.getKeyword());
        model.addAttribute("mode", command.getMode());

        return "admin/article-console";
    }

    @PostMapping("/console/article")
    public String submitArticleManagements(@Valid BoardManagementCommand command) {
        List<Long> articleIds = command.getTarget();
        switch (command.getOperation()) {
            case DELETE:
                articleManagerService.deleteArticles(articleIds);
                break;

            case RESTORE:
                articleManagerService.restoreArticles(articleIds);
                break;
        }

        return "redirect:/manage/console/article";
    }


    @GetMapping("/console/comment")
    public String commentManagementPage(@Valid CommentListCommand command,
                                        Model model){
        BoardCommentsDTO result;
        if(command.getKeyword().isBlank()){
            result = commentManagerService.searchCommentsByPages(command.getMode(), command.getKeyword(), command.getPageNum(), command.getPageSize());
        } else {
            result = commentManagerService.readComments(command.getPageNum(), command.getPageSize());
        }

        model.addAttribute("comments", result.getBoardComments());

        model.addAttribute("mode", command.getMode());
        model.addAttribute("keyword", command.getKeyword());

        model.addAttribute("currentPage", result.getCurrentPage());
        model.addAttribute("totalPage", result.getTotalPages());
        model.addAttribute("pageSize", command.getPageSize());

        return "admin/comment-console";
    }

    @PostMapping("/console/comment")
    public String submitCommentManagements(@Valid BoardManagementCommand command){
        List<Long> commentIds = command.getTarget();
        switch (command.getOperation()) {
            case DELETE:
                commentManagerService.deleteComments(commentIds);
                break;

            case RESTORE:
                commentManagerService.restoreComments(commentIds);
                break;
        }

        return "redirect:/manage/console/comment";

    }


    @GetMapping("/console/account")
    public String accountManagementPage(@Valid AccountListCommand command, Model model){
        BoardAccountsDTO accounts;
        if(command.getKeyword().isBlank()){
            accounts = accountManagerService.readAccounts(command.getPageNum(), command.getPageSize());
        } else {
            // TODO: implement search by date feature.
            accounts = accountManagerService.searchAccounts(command.getMode(), command.getKeyword(), command.getPageNum(), command.getPageSize());
        }

        model.addAttribute("accounts", accounts.getAccounts());
        model.addAttribute("currentPage", accounts.getCurrentPage());
        model.addAttribute("totalPage", accounts.getTotalPage());
        model.addAttribute("pageSize", command.getPageSize());
        model.addAttribute("keyword", command.getKeyword());
        model.addAttribute("mode", command.getMode());
        model.addAttribute("date", command.getDate());

        return "admin/account-console";
    }


    @PostMapping("/console/account")
    public String submitAccountManagements(@Valid AccountManagementCommand command) {
        List<Long> target = command.getTarget();
        switch (command.getOperation()) {
            case INVALIDATE:
                accountManagerService.invalidateAccounts(target);
                break;

            case RESTORE:
                accountManagerService.restoreAccounts(target);
                break;

            case CHANGE_PASSWORD:
                accountManagerService.changePassword(target, command.getKeyword());
                break;
                
            case CHANGE_USERNAME:
                accountManagerService.changeUsername(target, command.getKeyword());
                break;
        }

        return "redirect:/manage/console/account";
    }

}
