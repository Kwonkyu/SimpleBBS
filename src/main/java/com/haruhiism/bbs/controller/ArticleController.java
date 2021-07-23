package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.article.*;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentDTO;
import com.haruhiism.bbs.domain.dto.ResourceDTO;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.exception.auth.AuthenticationFailedException;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.comment.CommentService;
import com.haruhiism.bbs.service.file.FileHandlerService;
import com.haruhiism.bbs.service.manage.AccountManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
@Slf4j
public class ArticleController {

    private final ArticleService articleService;
    private final AccountManagerService accountManagerService;
    private final CommentService commentService;
    private final FileHandlerService fileHandlerService;


    private String getUsernameFromSecurityContext(SecurityContext context) {
        return isAnonymousUser(context) ? "" : ((BoardAccount) context.getAuthentication().getPrincipal()).getAlias();
    }

    private boolean isAnonymousUser(SecurityContext context) {
        return context.getAuthentication() instanceof AnonymousAuthenticationToken;
    }

    @GetMapping("/list")
    public String listBoardArticles(Model model,
                                    @ModelAttribute("command") @Valid ArticleListCommand command,
                                    BindingResult bindingResult,
                                    Principal principal){ // https://www.baeldung.com/get-user-in-spring-security

        if(bindingResult.hasErrors()){
            return "redirect:/board/list";
        }

        BoardArticleDTO.PagedArticles articles = command.getKeyword().isBlank() ?
                articleService.readAllByPages(command.getPageNum(), command.getPageSize()) :
                articleService.searchAllByPages(command.getMode(), command.getKeyword(), command.getPageNum(), command.getPageSize());

        model.addAttribute("articles", articles.getArticles());
        model.addAttribute("totalPages", articles.getPages());
        model.addAttribute("currentPage", articles.getCurrentPage());
        model.addAttribute("pageSize", command.getPageSize());
        model.addAttribute("searchMode", command.getMode().name());
        model.addAttribute("searchKeyword", command.getKeyword());

        Map<String, String> links = new LinkedHashMap<>();
        if(principal == null){
            links.put("LOGIN", "/account/login");
            links.put("REGISTER", "/account/register");
        } else {
            links.put("LOGOUT", "/account/logout");
            links.put("WITHDRAW", "/account/withdraw");
            links.put("MANAGE", "/account/manage");

            if(accountManagerService.authManagerAccess(principal.getName())){
                links.put("ADMIN CONSOLE", "/manage/console");
            }
        }

        model.addAttribute("links", links);
        return "board/list";
    }

    @GetMapping("/read")
    public String readBoardArticle(Model model,
                                   @Valid ArticleReadCommand command,
                                   BindingResult bindingResult,
                                   @CurrentSecurityContext SecurityContext context) {

        if (bindingResult.hasErrors()) {
            return "redirect:/board/list";
        }

        BoardArticleDTO article = articleService.readArticle(command.getId());
        BoardCommentDTO.PagedComments comments = commentService.readArticleCommentsPaged(article.getId(), command.getCommentPage(), 10);
        List<ResourceDTO> resources = fileHandlerService.listResourcesOfArticle(command.getId());

        model.addAttribute("loginUsername", getUsernameFromSecurityContext(context));
        model.addAttribute("article", article);
        model.addAttribute("comments", comments.getComments());
        model.addAttribute("currentCommentPage", comments.getCurrentPage());
        model.addAttribute("totalCommentPages", comments.getPages());
        model.addAttribute("resources", resources);

        return "board/read";
    }


    @GetMapping("/write")
    public String writeBoardArticle(
            Model model,
            @ModelAttribute("command") ArticleSubmitCommand command,
            @CurrentSecurityContext SecurityContext context){
        // https://stackoverflow.com/questions/57053736/how-to-check-if-user-is-logged-in-or-anonymous-in-spring-security
        model.addAttribute("loginUsername", getUsernameFromSecurityContext(context));
        return "board/write";
    }

    @PostMapping("/write")
    public String submitBoardArticle(
            Model model,
            @ModelAttribute("command") @Valid ArticleSubmitCommand command,
            BindingResult bindingResult,
            @CurrentSecurityContext SecurityContext context){
        if(bindingResult.hasErrors()){
            model.addAttribute("loginUsername", getUsernameFromSecurityContext(context));
            return "/board/write";
        }

        BoardArticleDTO articleDTO = new BoardArticleDTO(command);
        long createdArticleId;
        if(isAnonymousUser(context)) {
            createdArticleId = articleService.createArticle(articleDTO);
        } else {
            createdArticleId = articleService.createArticle(articleDTO, context.getAuthentication().getName());
        }
        fileHandlerService.store(command.getUploadedFiles(), createdArticleId);

        return "redirect:/board/read?id=" + createdArticleId;
    }


    private void addArticleToModel(Model model, Long articleID){
        model.addAttribute("article", articleService.readArticle(articleID));
        model.addAttribute("resources", fileHandlerService.listResourcesOfArticle(articleID));
    }

    private void authorizeAccountWrittenArticle(BoardArticleDTO article, Principal principal) {
        if(principal == null || !principal.getName().equals(article.getUserId()))
            throw new AuthenticationFailedException();
    }

    @GetMapping("/edit")
    public String requestEditArticle(Model model,
                                     @ModelAttribute("command") @Validated(ArticleEditRequestValidationGroup.class)
                                     ArticleEditRequestCommand command,
                                     BindingResult bindingResult,
                                     Principal principal){

        if(bindingResult.hasErrors()){
            return "redirect:/board/list";
        }

        BoardArticleDTO article = articleService.readArticle(command.getId());
        if (article.isWrittenByAccount()) {
            authorizeAccountWrittenArticle(article, principal);
            addArticleToModel(model, command.getId());
            return "board/edit";
        } else {
            return "board/editRequest";
        }
    }

    @PostMapping("/edit")
    public String authEditArticle(Model model,
                                  @ModelAttribute("command") @Validated(ArticleEditSubmitValidationGroup.class)
                                  ArticleEditRequestCommand command,
                                  BindingResult bindingResult){

        if (bindingResult.hasErrors()) {
            return "board/editRequest";
        }

        if(!articleService.authorizeArticleAccess(command.getId(), command.getPassword())) {
            bindingResult.addError(new FieldError("command", "password", "Password not matched."));
            return "board/editRequest";
        }

        addArticleToModel(model, command.getId());
        model.addAttribute("password", command.getPassword());
        return "board/edit";
    }


    @PostMapping("/edit/submit")
    public String submitEditArticle(
            @ModelAttribute("article") @Valid ArticleEditSubmitCommand command,
            BindingResult bindingResult,
            Principal principal){

        if (bindingResult.hasErrors()) {
            return "redirect:/board/list";
        }

        BoardArticleDTO article = articleService.readArticle(command.getId());
        if(article.isWrittenByAccount()) {
            authorizeAccountWrittenArticle(article, principal);
        } else {
            if (!articleService.authorizeArticleAccess(command.getId(), command.getPassword()))
                throw new AuthenticationFailedException();
        }

        articleService.updateArticle(new BoardArticleDTO(command));
        fileHandlerService.delete(command.getDelete(), command.getId());
        fileHandlerService.store(command.getUploadedFiles(), command.getId());
        return "redirect:/board/list";
    }


    @GetMapping("/remove")
    public String requestRemoveArticle(
            @ModelAttribute("command") @Validated(ArticleRemoveRequestValidationGroup.class)
            ArticleRemoveRequestCommand command,
            BindingResult bindingResult,
            Principal principal) {

        if (bindingResult.hasErrors()) {
            return "redirect:/board/list";
        }

        BoardArticleDTO article = articleService.readArticle(command.getId());
        if (article.isWrittenByAccount()) {
            authorizeAccountWrittenArticle(article, principal);
            articleService.deleteArticle(command.getId());
            return "redirect:/board/list";
        } else {
            return "board/removeRequest";
        }
    }

    @PostMapping("/remove")
    public String authRemoveArticle(@ModelAttribute("command") @Validated(ArticleRemoveSubmitValidationGroup.class)
                                    ArticleRemoveRequestCommand command,
                                    BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "board/removeRequest";
        }

        BoardArticleDTO article = articleService.readArticle(command.getId());
        if(article.isWrittenByAccount()) {
            throw new IllegalStateException("Unauthorized article access.");
        } else {
            if(articleService.authorizeArticleAccess(command.getId(), command.getPassword())) {
                articleService.deleteArticle(command.getId());
            } else {
                bindingResult.addError(new FieldError("command", "password", "Password not matched."));
                return "board/removeRequest";
            }
        }

        return "redirect:/board/list";
    }
}
