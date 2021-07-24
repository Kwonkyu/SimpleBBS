package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.DateBasedListCommand;
import com.haruhiism.bbs.command.article.ArticleListCommand;
import com.haruhiism.bbs.command.manage.*;
import com.haruhiism.bbs.domain.ManagerLevel;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentDTO;
import com.haruhiism.bbs.exception.account.NoAccountFoundException;
import com.haruhiism.bbs.service.account.AccountService;
import com.haruhiism.bbs.service.manage.AccountManagerService;
import com.haruhiism.bbs.service.manage.ArticleManagerService;
import com.haruhiism.bbs.service.manage.CommentManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
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


    private Map<String, String> generateConsoleLinksByAccountAuthorities(Collection<? extends GrantedAuthority> authorities){
        Map<String, String> links = new LinkedHashMap<>();
        links.put("BOARD", "/board/list");
        links.put("CONSOLE", "/manage/console");
        authorities.forEach(authority -> {
            ManagerLevel level = ManagerLevel.valueOf(authority.getAuthority());
            switch(level){
                case ACCOUNT_MANAGER:
                    links.put("ACCOUNTS", "/manage/console/account");
                    break;

                case BOARD_MANAGER:
                    links.put("ARTICLES", "/manage/console/article");
                    links.put("COMMENTS", "/manage/console/comment");
                    break;
            }
        });
        return links;
    }

    private void addDateCommandToModel(Model model, DateBasedListCommand command){
        model.addAttribute("keyword", command.getKeyword());
        model.addAttribute("from", command.getFrom());
        model.addAttribute("to", command.getTo());
        model.addAttribute("betweenDates", command.isBetweenDates());
        model.addAttribute("pageSize", command.getPageSize());
    }

    @GetMapping("/console")
    public String showManagementPage(Model model,
                                     @CurrentSecurityContext SecurityContext context){
        model.addAttribute("writtenArticles", articleManagerService.countAllArticles());
        model.addAttribute("deletedArticles", articleManagerService.countAllDeletedArticles());

        model.addAttribute("writtenComments", commentManagerService.countAllComments());
        model.addAttribute("deletedComments", commentManagerService.countAllDeletedComments());

        model.addAttribute("signedAccounts", accountManagerService.countAllAccounts());

        Authentication authentication = context.getAuthentication();
        model.addAttribute("userId", authentication.getName());
        model.addAttribute("links", generateConsoleLinksByAccountAuthorities(authentication.getAuthorities()));

        return "admin/management-console";
    }


    @GetMapping("/console/article")
    public String articleManagementPage(@Valid ArticleListCommand command,
                                        BindingResult bindingResult,
                                        Model model,
                                        @CurrentSecurityContext SecurityContext context){

        if(bindingResult.hasErrors()){
            return "redirect:/manage/console/article";
        }

        LocalDateTime from = command.isBetweenDates() ? LocalDateTime.of(command.getFrom(), LocalTime.of(0, 0)) : LocalDateTime.MIN;
        LocalDateTime to = command.isBetweenDates() ? LocalDateTime.of(command.getTo(), LocalTime.of(0, 0)) : LocalDateTime.now();

        BoardArticleDTO.PagedArticles result = command.getKeyword().isBlank() ?
                articleManagerService.readArticlesPage(command.getPageNum(), command.getPageSize(), from, to) :
                articleManagerService.searchArticlesPage(command.getMode(), command.getKeyword(), command.getPageNum(), command.getPageSize(), from, to);

        addDateCommandToModel(model, command);
        model.addAttribute("articles", result.getArticles());
        model.addAttribute("currentPage", result.getCurrentPage());
        model.addAttribute("totalPages", result.getPages());
        model.addAttribute("mode", command.getMode().name());
        model.addAttribute("links", generateConsoleLinksByAccountAuthorities(context.getAuthentication().getAuthorities()));

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
                                        @CurrentSecurityContext SecurityContext context){

        if (bindingResult.hasErrors()) {
            return "redirect:/manage/console/comment";
        }

        LocalDateTime from = command.isBetweenDates() ? LocalDateTime.of(command.getFrom(), LocalTime.of(0, 0)) : LocalDateTime.MIN;
        LocalDateTime to = command.isBetweenDates() ? LocalDateTime.of(command.getTo(), LocalTime.of(0, 0)) : LocalDateTime.now();

        BoardCommentDTO.PagedComments result = command.getKeyword().isBlank() ?
            commentManagerService.readCommentsPage(command.getPageNum(), command.getPageSize(), from, to) :
            commentManagerService.searchCommentsPage(command.getMode(), command.getKeyword(), command.getPageNum(), command.getPageSize(), from, to);

        addDateCommandToModel(model, command);
        model.addAttribute("currentPage", result.getCurrentPage());
        model.addAttribute("totalPages", result.getPages());
        model.addAttribute("comments", result.getComments());
        model.addAttribute("mode", command.getMode().name());
        model.addAttribute("links", generateConsoleLinksByAccountAuthorities(context.getAuthentication().getAuthorities()));

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
                                        @CurrentSecurityContext SecurityContext context){

        if (bindingResult.hasErrors()) {
            return "redirect:/manage/console/account";
        }

        LocalDateTime from = command.isBetweenDates() ? LocalDateTime.of(command.getFrom(), LocalTime.of(0,0)) : LocalDateTime.MIN;
        LocalDateTime to = command.isBetweenDates() ? LocalDateTime.of(command.getTo(), LocalTime.of(0, 0)) : LocalDateTime.now();

        BoardAccountDTO.PagedAccounts accounts = command.getKeyword().isBlank() ?
                accountManagerService.readAccountsPage(command.getPageNum(), command.getPageSize(), from, to) :
                accountManagerService.searchAccountsPage(command.getMode(), command.getKeyword(), command.getPageNum(), command.getPageSize(), from, to);

        addDateCommandToModel(model, command);
        model.addAttribute("currentPage", accounts.getCurrentPage());
        model.addAttribute("totalPages", accounts.getPages());
        model.addAttribute("accounts", accounts.getAccounts());
        model.addAttribute("mode", command.getMode().name());
        model.addAttribute("links", generateConsoleLinksByAccountAuthorities(context.getAuthentication().getAuthorities()));

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


    @GetMapping("/console/account/level")
    public String manageAccountLevels(Model model,
                                      @ModelAttribute("command")
                                      @Validated(AccountLevelManagementCommand.Request.class)
                                      AccountLevelManagementCommand command,
                                      BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return "redirect:/manage/console/account";
        }

        List<ManagerLevel> levels = accountService.getAccountManagerAuthorities(command.getId());
        model.addAttribute("accountLevels", levels);
        model.addAttribute("levels", ManagerLevel.values());

        return "admin/account-level-console";
    }

    @PostMapping("/console/account/level")
    public String submitAccountLevelManagement(Model model,
                                               @ModelAttribute("command")
                                               @Validated(AccountLevelManagementCommand.Group.class)
                                               AccountLevelManagementCommand command,
                                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "admin/account-level-console";
        }

        try {
            accountManagerService.changeManagerLevel(command.getId(), command.getLevelName(), command.getOperation());
        } catch (NoAccountFoundException exception) {
            return "redirect:/manage/console/account";
        }

        List<ManagerLevel> levels = accountService.getAccountManagerAuthorities(command.getId());
        model.addAttribute("accountLevels", levels);
        model.addAttribute("levels", ManagerLevel.values());

        return "admin/account-level-console";
    }

}
