package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.article.*;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentsDTO;
import com.haruhiism.bbs.exception.AuthenticationFailedException;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.exception.UpdateDeletedArticleException;
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
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class ArticleController {

    private final ArticleService articleService;
    private final CommentService commentService;

    private final String sessionAuthAttribute = "loginAuthInfo";


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

        // TODO: logged in users vs not logged in users.

        model.addAttribute("article", article);
        model.addAttribute("comments", comments.getBoardComments());
        model.addAttribute("currentCommentPage", comments.getCurrentPage());
        model.addAttribute("totalCommentPages", comments.getTotalPages());

        return "board/read";
    }


    @GetMapping("/write")
    // no validation here because it's writing a new article.
    public String writeBoardArticle(
            Model model,
            @ModelAttribute("command") ArticleSubmitCommand command,
            HttpServletRequest request){

        getLoginSessionInfoFromHttpSession(request.getSession(false))
        .ifPresent(l -> model.addAttribute("loginUsername", l.getUsername()));

        return "board/write";
    }

    @PostMapping("/write")
    public String submitBoardArticle(
            Model model,
            @ModelAttribute("command") @Valid ArticleSubmitCommand command,
            BindingResult bindingResult,
            HttpServletRequest request,
            HttpServletResponse response){

        Optional<LoginSessionInfo> loginSessionInfo = getLoginSessionInfoFromHttpSession(request.getSession(false));

        if(bindingResult.hasErrors()){
            // Validation should not be handled by exception handlers because of user feedback.
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
            loginSessionInfo.ifPresent(l -> model.addAttribute("loginUsername", l.getUsername()));
            return "/board/write";
        }

        BoardArticleDTO articleDTO = new BoardArticleDTO(
                command.getWriter(),
                command.getPassword(),
                command.getTitle(),
                command.getContent());

        // TODO: is it good code?
        articleService.createArticle(articleDTO, loginSessionInfo.orElse(null));
        return "redirect:/board/list";
    }


    @GetMapping("/edit")
    public String requestEditArticle(Model model,
                                     @Valid ArticleEditRequestCommand command,
                                     HttpServletRequest request){

        if (articleService.readArticle(command.getId()).getAccountId() == null) {
            model.addAttribute("id", command.getId());
            return "board/editRequest";
        } else {
            // TODO: throw proper exception? like bad request, access...
            // TODO: it's duplicated withe edit/submit.
            LoginSessionInfo loginSessionInfo = getLoginSessionInfoFromHttpSession(request.getSession(false))
                    .orElseThrow(AuthenticationFailedException::new);

            BoardArticleDTO accessArticleDTO = articleService.authArticleAccess(
                    command.getId(), loginSessionInfo.getAccountID())
                    .orElseThrow(AuthenticationFailedException::new);

            model.addAttribute("article", accessArticleDTO);
            return "board/edit";
        }
    }

    @PostMapping("/edit")
    public String authEditArticle(Model model, @ModelAttribute("command") @Valid ArticleEditAuthCommand command){
        BoardArticleDTO boardArticleDTO = articleService.authArticleAccess(
                command.getId(), command.getPassword())
                .orElseThrow(AuthenticationFailedException::new);
        model.addAttribute("article", boardArticleDTO);
        return "board/edit";
    }


    @PostMapping("/edit/submit")
    public String submitEditArticle(
            @Valid ArticleEditSubmitCommand command,
            HttpServletRequest request){

        // TODO: 컨트롤러의 인증 관련 로직을 서비스로 좀 이동할 필요가 있지 않을까?
        BoardArticleDTO editRequestedArticleDTO = articleService.readArticle(command.getArticleID());
        BoardArticleDTO editedArticleDTO = null;
        if(editRequestedArticleDTO.getAccountId() == null){
            editedArticleDTO = articleService.authArticleAccess(
                    command.getArticleID(), command.getPassword())
                    .orElseThrow(AuthenticationFailedException::new);
        } else {
            LoginSessionInfo loginSessionInfo = getLoginSessionInfoFromHttpSession(request.getSession(false))
                    .orElseThrow(AuthenticationFailedException::new);

            editedArticleDTO = articleService.authArticleAccess(
                    command.getArticleID(), loginSessionInfo.getAccountID())
                    .orElseThrow(AuthenticationFailedException::new);
        }

        editedArticleDTO.setTitle(command.getTitle());
        editedArticleDTO.setContent(command.getContent());

        articleService.updateArticle(editedArticleDTO);
        return "redirect:/board/list";
    }


    @GetMapping("/remove")
    public String requestRemoveArticle(
            Model model,
            @Valid ArticleRemoveRequestCommand command,
            HttpServletRequest request){
        BoardArticleDTO removeRequestedArticle = articleService.readArticle(command.getId());
        if (removeRequestedArticle.getAccountId() == null) {
            model.addAttribute("id", command.getId());
            return "board/removeRequest";
        } else {
            LoginSessionInfo loginSessionInfo = getLoginSessionInfoFromHttpSession(request.getSession(false))
                    .orElseThrow(AuthenticationFailedException::new);

            // TODO: 굳이 컨트롤러에서?
            Optional<BoardArticleDTO> articleAccess = articleService.authArticleAccess(command.getId(), loginSessionInfo.getAccountID());
            if (articleAccess.isPresent()) {
                articleService.deleteArticle(command.getId());
            } else {
                throw new AuthenticationFailedException();
            }

            return "redirect:/board/list";
        }
    }

    @PostMapping("/remove")
    public String authRemoveArticle(@Valid ArticleRemoveAuthCommand command){
        Optional<BoardArticleDTO> boardArticleDTO = articleService.authArticleAccess(command.getId(), command.getPassword());

        if(boardArticleDTO.isPresent()){
            // TODO: 댓글 삭제 로직을 서비스 계층으로 이동?
            commentService.deleteCommentsOfArticle(command.getId());
            articleService.deleteArticle(command.getId());
            return "redirect:/board/list";
        } else {
            throw new AuthenticationFailedException();
        }
    }

    private Optional<LoginSessionInfo> getLoginSessionInfoFromHttpSession(HttpSession session) {
        if(session == null) return Optional.empty();
        LoginSessionInfo loginSessionInfo = (LoginSessionInfo) session.getAttribute(sessionAuthAttribute);

        return loginSessionInfo == null ? Optional.empty() : Optional.of(loginSessionInfo);
    }
}
