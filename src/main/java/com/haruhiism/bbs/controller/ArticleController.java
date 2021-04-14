package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.article.*;
import com.haruhiism.bbs.domain.dto.BoardArticleAuthDTO;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentsDTO;
import com.haruhiism.bbs.exception.AuthenticationFailedException;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.authentication.LoginSessionInfo;
import com.haruhiism.bbs.service.comment.CommentService;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class ArticleController {

    private final ArticleService articleService;
    private final CommentService commentService;


    // TODO: pageSize option.
    @GetMapping("/list")
    public String listBoardArticles(Model model, @ModelAttribute("command") @Valid ArticleListCommand command){
        BoardArticlesDTO articlesDTO = articleService.readAllByPages(command.getPageNum(), command.getPageSize());

        model.addAttribute("articles", articlesDTO.getBoardArticles());
        model.addAttribute("commentSizes", articlesDTO.getBoardArticleCommentSize());
        model.addAttribute("currentPage", articlesDTO.getCurrentPage());
        model.addAttribute("totalPages", articlesDTO.getTotalPages());

        return "board/list";
    }


    @GetMapping("/search")
    public String searchArticles(Model model, @ModelAttribute("command") @Valid ArticleSearchCommand command) {
        BoardArticlesDTO searchedArticles = articleService.searchAllByPages(
                command.getMode(),
                command.getKeyword(),
                command.getPageNum(),
                command.getPageSize());
        model.addAttribute("articles", searchedArticles.getBoardArticles());
        model.addAttribute("commentSizes", searchedArticles.getBoardArticleCommentSize());
        model.addAttribute("currentPage", searchedArticles.getCurrentPage());
        model.addAttribute("totalPages", searchedArticles.getTotalPages());
        model.addAttribute("searchMode", command.getMode().name());
        model.addAttribute("searchKeyword", command.getKeyword());
        return "board/list";
    }


    @GetMapping("/read")
    public String readBoardArticle(Model model, @Valid ArticleReadCommand command) {
        BoardArticleDTO article = articleService.readArticle(command.getId());
        BoardCommentsDTO comments = commentService.readCommentsOfArticle(article.getId(), command.getCommentPage(), 10);

        model.addAttribute("article", article);
        model.addAttribute("comments", comments.getBoardComments());
        model.addAttribute("currentCommentPage", comments.getCurrentPage());
        model.addAttribute("totalCommentPages", comments.getTotalPages());

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

        BoardArticleDTO articleDTO = BoardArticleDTO.builder()
                .writer(command.getWriter())
                .password(command.getPassword())
                .title(command.getTitle())
                .content(command.getContent()).build();

        BoardArticleAuthDTO authDTO = BoardArticleAuthDTO.builder()
                .loginSessionInfo(loginSessionInfo).build();

        articleService.createArticle(articleDTO, authDTO);
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
                    command.getId(), BoardArticleAuthDTO.builder().loginSessionInfo(loginSessionInfo).build())
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
                command.getId(), BoardArticleAuthDTO.builder().rawPassword(command.getPassword()).build())
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

        BoardArticleAuthDTO authDTO = BoardArticleAuthDTO.builder()
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
                    BoardArticleAuthDTO.builder()
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
                BoardArticleAuthDTO.builder()
                        .rawPassword(command.getPassword())
                        .build());

        return "redirect:/board/list";
    }

    private LoginSessionInfo getLoginSessionInfoFromHttpSession(HttpSession session) {
        if(session == null) return null;
        String sessionAuthAttribute = "loginAuthInfo";
        return (LoginSessionInfo) session.getAttribute(sessionAuthAttribute);
    }
}
