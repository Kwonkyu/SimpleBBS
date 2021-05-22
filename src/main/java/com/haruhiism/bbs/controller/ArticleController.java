package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.article.*;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.dto.*;
import com.haruhiism.bbs.exception.auth.AuthenticationFailedException;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.comment.CommentService;
import com.haruhiism.bbs.service.file.FileHandlerService;
import com.haruhiism.bbs.service.manage.AccountManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
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


    @GetMapping("/list")
    public String listBoardArticles(Model model,
                                    @ModelAttribute("command") @Valid ArticleListCommand command,
                                    BindingResult bindingResult,
                                    HttpServletRequest request){

        if(bindingResult.hasErrors()){
            return "redirect:/board/list";
        }

        BoardArticlesDTO articlesDTO = command.getKeyword().isBlank() ?
                articleService.readAllByPages(command.getPageNum(), command.getPageSize()) :
                articleService.searchAllByPages(command.getMode(), command.getKeyword(), command.getPageNum(), command.getPageSize());

        model.addAttribute("articles", articlesDTO.getBoardArticles());
        model.addAttribute("commentSizes", articlesDTO.getBoardArticleCommentSize());
        model.addAttribute("totalPages", articlesDTO.getTotalPages());
        model.addAttribute("currentPage", command.getPageNum());
        model.addAttribute("pageSize", command.getPageSize());
        model.addAttribute("searchMode", command.getMode().name());
        model.addAttribute("searchKeyword", command.getKeyword());

        HttpSession session = request.getSession(false);
        Map<String, String> links = new LinkedHashMap<>();
        if(session == null){
            links.put("LOGIN", "/account/login");
            links.put("REGISTER", "/account/register");
        } else {
            links.put("LOGOUT", "/account/logout");
            links.put("WITHDRAW", "/account/withdraw");
            links.put("MANAGE", "/account/manage");

            String userId = ((LoginSessionInfo)session.getAttribute("loginSessionInfo")).getUserID();
            if(accountManagerService.authManagerAccess(userId)){
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
                                   HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "redirect:/board/list";
        }

        BoardArticleDTO article = articleService.readArticle(command.getId());
        BoardCommentsDTO comments = commentService.readCommentsOfArticle(article.getId(), command.getCommentPage(), 10);
        List<ResourceDTO> resources = fileHandlerService.listResourcesOfArticle(command.getId());

        LoginSessionInfo loginSessionInfo = getLoginSessionInfoFromHttpSession(request.getSession(false));
        model.addAttribute("loginUsername", loginSessionInfo == null ? "" : loginSessionInfo.getUsername());

        model.addAttribute("article", article);
        model.addAttribute("comments", comments.getBoardComments());
        model.addAttribute("currentCommentPage", comments.getCurrentPage());
        model.addAttribute("totalCommentPages", comments.getTotalPages());
        model.addAttribute("resources", resources);

        return "board/read";
    }


    @GetMapping("/write")
    public String writeBoardArticle(
            Model model,
            @ModelAttribute("command") ArticleSubmitCommand command,
            HttpServletRequest request){

        LoginSessionInfo loginSessionInfo = getLoginSessionInfoFromHttpSession(request.getSession(false));
        model.addAttribute("loginUsername", loginSessionInfo == null ? "" : loginSessionInfo.getUsername());

        return "board/write";
    }

    @PostMapping("/write")
    public String submitBoardArticle(
            Model model,
            @ModelAttribute("command") @Valid ArticleSubmitCommand command,
            BindingResult bindingResult,
            HttpServletRequest request){

        LoginSessionInfo loginSessionInfo = getLoginSessionInfoFromHttpSession(request.getSession(false));

        if(bindingResult.hasErrors()){
            model.addAttribute("loginUsername", loginSessionInfo == null ? "" : loginSessionInfo.getUsername());
            return "/board/write";
        }

        BoardArticleDTO articleDTO = new BoardArticleDTO(command);
        AuthDTO authDTO = AuthDTO.builder().loginSessionInfo(loginSessionInfo).build();
        Long createdArticleId = articleService.createArticle(articleDTO, authDTO);
        fileHandlerService.store(command.getUploadedFiles(), createdArticleId);

        return "redirect:/board/list";
    }


    private boolean isArticleWrittenByLoggedInAccount(Long articleId){
        return !articleService.readArticle(articleId).getUserId().isBlank();
    }

    private void addArticleToModel(Model model, Long articleID, AuthDTO authDTO){
        model.addAttribute("article", articleService.authArticleEdit(articleID, authDTO).orElseThrow(AuthenticationFailedException::new));
        model.addAttribute("resources", fileHandlerService.listResourcesOfArticle(articleID));
    }


    @GetMapping("/edit")
    public String requestEditArticle(Model model,
                                     @ModelAttribute("command") @Validated(ArticleEditRequestValidationGroup.class) ArticleEditRequestCommand command,
                                     BindingResult bindingResult,
                                     HttpServletRequest request){

        if(bindingResult.hasErrors()){
            return "redirect:/board/list";
        }

        if (isArticleWrittenByLoggedInAccount(command.getId())) {
            LoginSessionInfo loginSessionInfo = getLoginSessionInfoFromHttpSession(request.getSession(false));
            if(loginSessionInfo == null){
                throw new AuthenticationFailedException();
            }

            addArticleToModel(model, command.getId(), AuthDTO.builder().loginSessionInfo(loginSessionInfo).build());
            model.addAttribute("password", "THIS_IS_USELESS_PASSWORD");
            return "board/edit";
        } else {
            return "board/editRequest";
        }
    }

    @PostMapping("/edit")
    public String authEditArticle(Model model,
                                  @ModelAttribute("command") @Validated(ArticleEditSubmitValidationGroup.class) ArticleEditRequestCommand command,
                                  BindingResult bindingResult){

        if (bindingResult.hasErrors()) {
            return "board/editRequest";
        }

        try {
            addArticleToModel(model, command.getId(), AuthDTO.builder().rawPassword(command.getPassword()).build());
        } catch (AuthenticationFailedException ex){
            bindingResult.addError(new FieldError("command", "password", "Password not matched."));
            return "board/editRequest";
        }

        model.addAttribute("password", command.getPassword());
        return "board/edit";
    }


    @PostMapping("/edit/submit")
    public String submitEditArticle(
            Model model,
            @ModelAttribute("article") @Valid ArticleEditSubmitCommand command,
            BindingResult bindingResult,
            HttpServletRequest request){

        if (bindingResult.hasErrors()) {
            model.addAttribute("password", command.getPassword());
            model.addAttribute("resources", fileHandlerService.listResourcesOfArticle(command.getId()));
            return "board/edit";
        }

        BoardArticleDTO editedArticleDTO = BoardArticleDTO.builder()
                .id(command.getId())
                .title(command.getTitle())
                .content(command.getContent()).build();

        AuthDTO authDTO = AuthDTO.builder()
                .rawPassword(command.getPassword())
                .loginSessionInfo(getLoginSessionInfoFromHttpSession(request.getSession(false))).build();

        articleService.updateArticle(editedArticleDTO, authDTO);
        fileHandlerService.delete(command.getDelete(), command.getId());
        fileHandlerService.store(command.getUploadedFiles(), command.getId());
        return "redirect:/board/list";
    }


    @GetMapping("/remove")
    public String requestRemoveArticle(
            Model model,
            @ModelAttribute("command") @Validated(ArticleRemoveRequestValidationGroup.class) ArticleRemoveRequestCommand command,
            BindingResult bindingResult,
            HttpServletRequest request){

        if (bindingResult.hasErrors()) {
            return "redirect:/board/list";
        }

        if (isArticleWrittenByLoggedInAccount(command.getId())) {
            articleService.deleteArticle(
                    command.getId(),
                    AuthDTO.builder().loginSessionInfo(getLoginSessionInfoFromHttpSession(request.getSession(false))).build());
            return "redirect:/board/list";
        } else {
            return "board/removeRequest";
        }
    }

    @PostMapping("/remove")
    public String authRemoveArticle(@ModelAttribute("command") @Validated(ArticleRemoveSubmitValidationGroup.class) ArticleRemoveRequestCommand command,
                                    BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "board/removeRequest";
        }

        try {
            articleService.deleteArticle(
                    command.getId(),
                    AuthDTO.builder()
                            .rawPassword(command.getPassword())
                            .build());
        } catch (AuthenticationFailedException exception) {
            bindingResult.addError(new FieldError("command", "password", "Password not matched."));
            return "board/removeRequest";
        }

        return "redirect:/board/list";
    }

    private LoginSessionInfo getLoginSessionInfoFromHttpSession(HttpSession session) {
        if(session == null) return null;
        String sessionAuthAttribute = "loginSessionInfo";
        return (LoginSessionInfo) session.getAttribute(sessionAuthAttribute);
    }
}
