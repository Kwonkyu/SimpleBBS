package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.DateBasedListCommand;
import com.haruhiism.bbs.command.article.ArticleListCommand;
import com.haruhiism.bbs.command.manage.AccountListCommand;
import com.haruhiism.bbs.command.manage.AccountManagementCommand;
import com.haruhiism.bbs.command.manage.BoardManagementCommand;
import com.haruhiism.bbs.command.manage.CommentListCommand;
import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.dto.*;
import com.haruhiism.bbs.service.account.AccountService;
import com.haruhiism.bbs.service.manage.AccountManagerService;
import com.haruhiism.bbs.service.manage.ArticleManagerService;
import com.haruhiism.bbs.service.manage.CommentManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/manage")
@RequiredArgsConstructor
@Slf4j
public class ManageController {

    private final ArticleManagerService articleManagerService;
    private final CommentManagerService commentManagerService;
    private final AccountManagerService accountManagerService;

    private final AccountService accountService;

    private final String sessionAuthAttribute = "loginSessionInfo";


    private Map<String, String> generateConsoleLinksByAccountLevel(List<AccountLevel> levels){
        Map<String, String> links = new LinkedHashMap<>();
        links.put("BOARD", "/board/list");
        links.put("CONSOLE", "/manage/console");
        for (AccountLevel level : levels) {
            switch(level){
                case ACCOUNT_MANAGER:
                    links.put("ACCOUNTS", "/manage/console/account");
                    break;

                case BOARD_MANAGER:
                    links.put("ARTICLES", "/manage/console/article");
                    links.put("COMMENTS", "/manage/console/comment");
                    break;
            }
        }

        return links;
    }

    private void addPaginationToModel(Model model, DTOContainer result){
        model.addAttribute("currentPage", result.getCurrentPage());
        model.addAttribute("totalPages", result.getTotalPages());
    }

    private void addDateCommandToModel(Model model, DateBasedListCommand command){
        model.addAttribute("pageSize", command.getPageSize());
        model.addAttribute("keyword", command.getKeyword());
        model.addAttribute("from", command.getFrom());
        model.addAttribute("to", command.getTo());
        model.addAttribute("betweenDates", command.isBetweenDates());
    }

    @GetMapping("/console")
    public String showManagementPage(Model model,
                                     @SessionAttribute(sessionAuthAttribute)LoginSessionInfo loginSessionInfo){
        model.addAttribute("writtenArticles", articleManagerService.countAllArticles());
        model.addAttribute("deletedArticles", articleManagerService.countAllDeletedArticles());

        model.addAttribute("writtenComments", commentManagerService.countAllComments());
        model.addAttribute("deletedComments", commentManagerService.countAllDeletedComments());

        model.addAttribute("signedAccounts", accountManagerService.countAllAccounts());

        model.addAttribute("userInfo", loginSessionInfo);
        List<AccountLevel> levels = accountService.getAccountLevels(BoardAccountDTO.builder().userId(loginSessionInfo.getUserID()).build()).getLevels();
        model.addAttribute("links", generateConsoleLinksByAccountLevel(levels));

        return "admin/management-console";
    }


    @GetMapping("/console/article")
    public String articleManagementPage(@Valid ArticleListCommand command,
                                        BindingResult bindingResult,
                                        Model model,
                                        @SessionAttribute(sessionAuthAttribute)LoginSessionInfo loginSessionInfo){

        if(bindingResult.hasErrors()){
            return "redirect:/manage/console/article";
        }

        BoardArticlesDTO result;
        if(command.getKeyword().isBlank()) {
            // TODO: ArticleService에 파라미터로 검색 조건을 설정하는 방식으로 통합?
            result = articleManagerService.readArticles(
                    command.getPageNum(), command.getPageSize(),
                    command.isBetweenDates() ? LocalDateTime.of(command.getFrom(), LocalTime.of(0, 0)) : LocalDateTime.MIN,
                    command.isBetweenDates() ? LocalDateTime.of(command.getTo(), LocalTime.of(0, 0)) : LocalDateTime.now());
        } else {
            result = articleManagerService.searchArticles(
                    command.getMode(), command.getKeyword(), command.getPageNum(), command.getPageSize(),
                    command.isBetweenDates() ? LocalDateTime.of(command.getFrom(), LocalTime.of(0, 0)) : LocalDateTime.MIN,
                    command.isBetweenDates() ? LocalDateTime.of(command.getTo(), LocalTime.of(0, 0)) : LocalDateTime.now());
        }

        addPaginationToModel(model, result);
        addDateCommandToModel(model, command);

        model.addAttribute("articles", result.getBoardArticles());
        model.addAttribute("commentSizes", result.getBoardArticleCommentSize());
        model.addAttribute("mode", command.getMode().name());

        List<AccountLevel> levels = accountService.getAccountLevels(BoardAccountDTO.builder().userId(loginSessionInfo.getUserID()).build()).getLevels();
        model.addAttribute("links", generateConsoleLinksByAccountLevel(levels));

        return "admin/article-console";
    }

    @PostMapping("/console/article")
    public String submitArticleManagements(@Valid BoardManagementCommand command,
                                           BindingResult bindingResult) {

        if (!bindingResult.hasErrors()) {
            List<Long> articleIds = command.getTarget();
            switch (command.getOperation()) {
                case DELETE:
                    articleManagerService.deleteArticles(articleIds);
                    break;

                case RESTORE:
                    articleManagerService.restoreArticles(articleIds);
                    break;
            }
        }

        return "redirect:/manage/console/article";
    }


    @GetMapping("/console/comment")
    public String commentManagementPage(@Valid CommentListCommand command,
                                        BindingResult bindingResult,
                                        Model model,
                                        @SessionAttribute(sessionAuthAttribute)LoginSessionInfo loginSessionInfo){

        if (bindingResult.hasErrors()) {
            return "redirect:/manage/console/comment";
        }

        BoardCommentsDTO result;
        if(command.getKeyword().isBlank()){
            result = commentManagerService.readComments(
                    command.getPageNum(), command.getPageSize(),
                    command.isBetweenDates() ? LocalDateTime.of(command.getFrom(), LocalTime.of(0, 0)) : LocalDateTime.MIN,
                    command.isBetweenDates() ? LocalDateTime.of(command.getTo(), LocalTime.of(0, 0)) : LocalDateTime.now());
        } else {
            result = commentManagerService.searchComments(
                    command.getMode(), command.getKeyword(), command.getPageNum(), command.getPageSize(),
                    command.isBetweenDates() ? LocalDateTime.of(command.getFrom(), LocalTime.of(0, 0)) : LocalDateTime.MIN,
                    command.isBetweenDates() ? LocalDateTime.of(command.getTo(), LocalTime.of(0, 0)) : LocalDateTime.now());
        }

        addPaginationToModel(model, result);
        addDateCommandToModel(model, command);
        model.addAttribute("comments", result.getBoardComments());
        model.addAttribute("mode", command.getMode().name());

        List<AccountLevel> levels = accountService.getAccountLevels(BoardAccountDTO.builder().userId(loginSessionInfo.getUserID()).build()).getLevels();
        model.addAttribute("links", generateConsoleLinksByAccountLevel(levels));

        return "admin/comment-console";
    }

    @PostMapping("/console/comment")
    public String submitCommentManagements(@Valid BoardManagementCommand command,
                                           BindingResult bindingResult){

        if (!bindingResult.hasErrors()) {
            List<Long> commentIds = command.getTarget();
            switch (command.getOperation()) {
                case DELETE:
                    commentManagerService.deleteComments(commentIds);
                    break;

                case RESTORE:
                    commentManagerService.restoreComments(commentIds);
                    break;
            }
        }

        return "redirect:/manage/console/comment";
    }


    @GetMapping("/console/account")
    public String accountManagementPage(@Valid AccountListCommand command,
                                        BindingResult bindingResult,
                                        Model model,
                                        @SessionAttribute(sessionAuthAttribute)LoginSessionInfo loginSessionInfo){

        if (bindingResult.hasErrors()) {
            return "redirect:/manage/console/account";
        }

        BoardAccountsDTO accounts;
        if(command.getKeyword().isBlank()){
            accounts = accountManagerService.readAccounts(
                    command.getPageNum(), command.getPageSize(),
                    command.isBetweenDates() ? LocalDateTime.of(command.getFrom(), LocalTime.of(0,0)) : LocalDateTime.MIN,
                    command.isBetweenDates() ? LocalDateTime.of(command.getTo(), LocalTime.of(0, 0)) : LocalDateTime.now());
        } else {
            accounts = accountManagerService.searchAccounts(
                    command.getMode(), command.getKeyword(), command.getPageNum(), command.getPageSize(),
                    command.isBetweenDates() ? LocalDateTime.of(command.getFrom(), LocalTime.of(0,0)) : LocalDateTime.MIN,
                    command.isBetweenDates() ? LocalDateTime.of(command.getTo(), LocalTime.of(0,0)) : LocalDateTime.now());
        }

        addPaginationToModel(model, accounts);
        addDateCommandToModel(model, command);

        model.addAttribute("accounts", accounts.getAccounts());
        model.addAttribute("mode", command.getMode().name());

        List<AccountLevel> levels = accountService.getAccountLevels(BoardAccountDTO.builder().userId(loginSessionInfo.getUserID()).build()).getLevels();
        model.addAttribute("links", generateConsoleLinksByAccountLevel(levels));

        return "admin/account-console";
    }


    @PostMapping("/console/account")
    public String submitAccountManagements(@Valid AccountManagementCommand command,
                                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "redirect:/manage/console/account";
        }

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
