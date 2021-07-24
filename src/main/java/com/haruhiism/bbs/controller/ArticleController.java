package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.article.ArticleListCommand;
import com.haruhiism.bbs.domain.dto.BoardCommentDTO;
import com.haruhiism.bbs.domain.dto.ResourceDTO;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.comment.CommentService;
import com.haruhiism.bbs.service.file.FileHandlerService;
import com.haruhiism.bbs.service.manage.AccountManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.haruhiism.bbs.domain.dto.BoardArticleDTO.*;

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

        PagedArticles articles = command.getKeyword().isBlank() ?
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
                                   @RequestParam(name = "id") long id,
                                   @RequestParam(name = "commentPage", defaultValue = "0") int commentPage,
                                   @CurrentSecurityContext SecurityContext context) {

        if (id <= 0 || commentPage < 0) return "redirect:/board/list";

        Read article = articleService.readArticle(id);
        BoardCommentDTO.PagedComments comments = commentService.readArticleCommentsPaged(id, commentPage, 10);
        List<ResourceDTO> resources = fileHandlerService.listResourcesOfArticle(id);

        model.addAttribute("loginUsername", getUsernameFromSecurityContext(context));
        model.addAttribute("authorized", article.isAnonymous() || article.getUserId().equals(context.getAuthentication().getName()));
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
            @ModelAttribute("command") Submit command,
            @CurrentSecurityContext SecurityContext context){
        // https://stackoverflow.com/questions/57053736/how-to-check-if-user-is-logged-in-or-anonymous-in-spring-security
        model.addAttribute("loginUsername", getUsernameFromSecurityContext(context));
        return "board/write";
    }

    @PostMapping("/write")
    public String submitBoardArticle(
            Model model,
            @ModelAttribute("command") @Validated(Submit.Create.class) Submit command,
            BindingResult bindingResult,
            @CurrentSecurityContext SecurityContext context) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("loginUsername", getUsernameFromSecurityContext(context));
            return "/board/write";
        }

        Submit articleDTO = Submit.builder()
                .writer(command.getWriter())
                .title(command.getTitle())
                .content(command.getContent())
                .password(command.getPassword())
                .build();

        long createdArticleId;
        if (isAnonymousUser(context)) {
            createdArticleId = articleService.createArticle(articleDTO);
        } else {
            createdArticleId = articleService.createArticle(articleDTO, context.getAuthentication().getName());
        }
        fileHandlerService.store(command.getUploadedFiles(), createdArticleId);

        return "redirect:/board/read?id=" + createdArticleId;
    }

    @GetMapping("/edit")
    public String requestEditArticle(Model model,
                                     @Validated(Authorize.Request.class) @ModelAttribute("command") Authorize command,
                                     BindingResult bindingResult,
                                     Principal principal){

        if(bindingResult.hasErrors()) return "redirect:/board/list";

        Read article = articleService.readArticle(command.getId());
        if(article.isAnonymous()) return "board/editRequest";
        if(principal != null && article.getUserId().equals(principal.getName())) {
            Submit edit = Submit.builder()
                    .id(article.getId())
                    .writer(article.getWriter())
                    .title(article.getTitle())
                    .content(article.getContent())
                    .password("PASSWORD_IS_ENCODED").build();

            model.addAttribute("article", edit);
            model.addAttribute("resources", fileHandlerService.listResourcesOfArticle(command.getId()));
            return "board/edit";
        }

        return "redirect:/board/list";
    }

    @PostMapping("/edit")
    public String authEditArticle(Model model,
                                  @Validated(Authorize.Submit.class) @ModelAttribute("command") Authorize command,
                                  BindingResult bindingResult){

        if (bindingResult.hasErrors()) {
            return "board/editRequest";
        }

        if(!articleService.authorizeAnonymousArticleAccess(command.getId(), command.getPassword())) {
            bindingResult.addError(new FieldError("command", "password", "Password not matched."));
            return "board/editRequest";
        }

        Read read = articleService.readArticle(command.getId());
        Submit article = Submit.builder()
                .id(read.getId())
                .title(read.getTitle())
                .writer(read.getWriter())
                .password(command.getPassword()) // not anonymous: doesn't need password. but anonymous does because of submit auth.
                .content(read.getContent())
                .build();

        model.addAttribute("article", article);
        model.addAttribute("resources", fileHandlerService.listResourcesOfArticle(command.getId()));
        model.addAttribute("password", command.getPassword());
        return "board/edit";
    }


    @PostMapping("/edit/submit")
    public String submitEditArticle(
            Model model,
            @ModelAttribute("article") @Validated(Submit.Update.class) Submit command,
            BindingResult bindingResult,
            Principal principal){

        Read article = articleService.readArticle(command.getId());

        if (bindingResult.hasErrors()) {
            model.addAttribute("resources", fileHandlerService.listResourcesOfArticle(command.getId()));
            return "board/edit";
        }

        if(article.isAnonymous() && articleService.authorizeAnonymousArticleAccess(command.getId(), command.getPassword()) ||
            principal != null && article.getUserId().equals(principal.getName())) {
            articleService.updateArticle(command);
            fileHandlerService.delete(command.getDelete(), command.getId());
            fileHandlerService.store(command.getUploadedFiles(), command.getId());
        }

        return "redirect:/board/list";
    }


    @GetMapping("/remove")
    public String requestRemoveArticle(
            @Validated(Authorize.Request.class) @ModelAttribute("command") Authorize command,
            BindingResult bindingResult,
            Principal principal) {

        if (bindingResult.hasErrors()) return "redirect:/board/list";

        Read article = articleService.readArticle(command.getId());
        if (article.isAnonymous()) return "board/removeRequest";
        if (principal != null && article.getUserId().equals(principal.getName())) articleService.deleteArticle(command.getId());
        return "redirect:/board/list";
    }

    @PostMapping("/remove")
    public String authRemoveArticle(@ModelAttribute("command") @Validated(Authorize.Submit.class) Authorize command,
                                    BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "board/removeRequest";
        }

        if(articleService.authorizeAnonymousArticleAccess(command.getId(), command.getPassword())) {
            articleService.deleteArticle(command.getId());
            return "redirect:/board/list";
        } else {
            bindingResult.addError(new FieldError("command", "password", "Password not matched."));
            return "board/removeRequest";
        }
    }
}
