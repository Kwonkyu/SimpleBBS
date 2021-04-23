package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.article.*;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.dto.*;
import com.haruhiism.bbs.exception.AuthenticationFailedException;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.comment.CommentService;
import com.haruhiism.bbs.service.file.FileHandlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class ArticleController {

    private final ArticleService articleService;
    private final CommentService commentService;

    private final FileHandlerService fileHandlerService;


    @GetMapping("/list")
    public String listBoardArticles(Model model, @ModelAttribute("command") @Valid ArticleListCommand command){
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

        return "board/list";
    }

    @GetMapping("/read")
    public String readBoardArticle(Model model,
                                   @Valid ArticleReadCommand command,
                                   HttpServletRequest request) {
        BoardArticleDTO article = articleService.readArticle(command.getId());
        BoardCommentsDTO comments = commentService.readCommentsOfArticle(article.getId(), command.getCommentPage(), 10);
        List<ResourceDTO> resources = fileHandlerService.listResourcesOfArticle(command.getId());

        LoginSessionInfo loginSessionInfo = getLoginSessionInfoFromHttpSession(request.getSession(false));
        if(loginSessionInfo != null) model.addAttribute("loginUsername", loginSessionInfo.getUsername());

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
        if(loginSessionInfo != null){
            model.addAttribute("loginUsername", loginSessionInfo.getUsername());
        }

        return "board/write";
    }

    @PostMapping("/write")
    public String submitBoardArticle(
            Model model,
            @ModelAttribute("command") @Valid ArticleSubmitCommand command,
            BindingResult bindingResult,
            HttpServletRequest request,
            HttpServletResponse response){

        LoginSessionInfo loginSessionInfo = getLoginSessionInfoFromHttpSession(request.getSession(false));

        if(bindingResult.hasErrors()){
            // Validation should not be handled by exception handlers because of user feedback.
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
            if(loginSessionInfo != null) {
                model.addAttribute("loginUsername", loginSessionInfo.getUsername());
            }
            return "/board/write";
        }

        BoardArticleDTO articleDTO = new BoardArticleDTO(command);
        AuthDTO authDTO = AuthDTO.builder().loginSessionInfo(loginSessionInfo).build();
        Long createdArticleId = articleService.createArticle(articleDTO, authDTO);
        fileHandlerService.store(command.getUploadedFiles(), createdArticleId);

        return "redirect:/board/list";
    }


    private boolean isArticleWrittenByLoggedInAccount(Long articleId){
        return articleService.readArticle(articleId).isWrittenByAccount();
    }

    @GetMapping("/edit")
    public String requestEditArticle(Model model,
                                     @Valid ArticleEditRequestCommand command,
                                     HttpServletRequest request){

        if (isArticleWrittenByLoggedInAccount(command.getId())) {
            LoginSessionInfo loginSessionInfo = getLoginSessionInfoFromHttpSession(request.getSession(false));
            if(loginSessionInfo == null){
                throw new AuthenticationFailedException();
            }

            BoardArticleDTO accessArticleDTO = articleService.authArticleEdit(
                    command.getId(), AuthDTO.builder().loginSessionInfo(loginSessionInfo).build())
                    .orElseThrow(AuthenticationFailedException::new);

            model.addAttribute("article", accessArticleDTO);
            return "board/edit";
        } else {
            model.addAttribute("id", command.getId());
            return "board/editRequest";
        }
    }

    @PostMapping("/edit")
    public String authEditArticle(Model model,
                                  @ModelAttribute("command") @Valid ArticleEditAuthCommand command){
        BoardArticleDTO boardArticleDTO = articleService.authArticleEdit(
                command.getId(), AuthDTO.builder().rawPassword(command.getPassword()).build())
                .orElseThrow(AuthenticationFailedException::new);

        model.addAttribute("article", boardArticleDTO);
        return "board/edit";
    }


    @PostMapping("/edit/submit")
    public String submitEditArticle(
            @Valid ArticleEditSubmitCommand command,
            HttpServletRequest request){

        BoardArticleDTO editedArticleDTO = BoardArticleDTO.builder()
                .id(command.getArticleID())
                .title(command.getTitle())
                .content(command.getContent()).build();

        AuthDTO authDTO = AuthDTO.builder()
                .rawPassword(command.getPassword())
                .loginSessionInfo(getLoginSessionInfoFromHttpSession(request.getSession(false))).build();

        articleService.updateArticle(editedArticleDTO, authDTO);
        return "redirect:/board/list";
    }


    @GetMapping("/remove")
    public String requestRemoveArticle(
            Model model,
            @Valid ArticleRemoveRequestCommand command,
            HttpServletRequest request){

        if (isArticleWrittenByLoggedInAccount(command.getId())) {
            articleService.deleteArticle(
                    command.getId(),
                    AuthDTO.builder()
                            .loginSessionInfo(getLoginSessionInfoFromHttpSession(request.getSession(false)))
                            .build());
            return "redirect:/board/list";
        } else {
            model.addAttribute("id", command.getId());
            return "board/removeRequest";
        }
    }

    @PostMapping("/remove")
    public String authRemoveArticle(@Valid ArticleRemoveAuthCommand command){

        articleService.deleteArticle(
                command.getId(),
                AuthDTO.builder()
                        .rawPassword(command.getPassword())
                        .build());

        return "redirect:/board/list";
    }

    private LoginSessionInfo getLoginSessionInfoFromHttpSession(HttpSession session) {
        if(session == null) return null;
        String sessionAuthAttribute = "loginSessionInfo";
        return (LoginSessionInfo) session.getAttribute(sessionAuthAttribute);
    }
}
