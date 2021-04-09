package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.command.article.*;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentsDTO;
import com.haruhiism.bbs.exception.AuthenticationFailedException;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.exception.UpdateDeletedArticleException;
import com.haruhiism.bbs.service.article.ArticleService;
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

import javax.servlet.http.HttpServletResponse;
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
    // no validation here because it's writing a new article.
    public String writeBoardArticle(@ModelAttribute("command") ArticleSubmitCommand command){
        return "board/write";
    }

    @PostMapping("/write")
    public String submitBoardArticle(@ModelAttribute("command") @Valid ArticleSubmitCommand command, BindingResult bindingResult, HttpServletResponse response){
        if(bindingResult.hasErrors()){
            // Validation should not be handled by exception handlers because of user feedback.
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
            return "board/write";
        }

        articleService.createArticle(new BoardArticleDTO(
                command.getWriter(),
                command.getPassword(),
                command.getTitle(),
                command.getContent())
        );
        return "redirect:/board/list";
    }


    @GetMapping("/edit")
    public String requestEditArticle(Model model, @Valid ArticleEditRequestCommand command){
        model.addAttribute("id", command.getId());
        return "board/editRequest";
    }

    @PostMapping("/edit")
    public String authEditArticle(Model model, @ModelAttribute("command") @Valid ArticleEditAuthCommand command){
        if(articleService.authArticleAccess(command.getId(), command.getPassword())){
            BoardArticleDTO article = articleService.readArticle(command.getId());
            article.setPassword(command.getPassword());
            model.addAttribute("article", article);
            return "board/edit";
        } else {
            throw new AuthenticationFailedException();
        }
    }


    @PostMapping("/edit/submit")
    public String submitEditArticle(@Valid ArticleEditSubmitCommand command){
        if(articleService.authArticleAccess(command.getArticleID(), command.getPassword())) {
            try {
                articleService.updateArticle(new BoardArticleDTO(
                        command.getArticleID(),
                        command.getWriter(),
                        command.getPassword(),
                        command.getTitle(),
                        command.getContent()));
            } catch (NoArticleFoundException e){
                throw new UpdateDeletedArticleException();
            }
        } else {
            throw new AuthenticationFailedException();
        }

        return "redirect:/board/list";
    }


    @GetMapping("/remove")
    public String requestRemoveArticle(Model model, @Valid ArticleRemoveRequestCommand command){
        model.addAttribute("id", command.getId());
        return "board/removeRequest";
    }

    @PostMapping("/remove")
    public String authRemoveArticle(@Valid ArticleRemoveAuthCommand command){
        if(articleService.authArticleAccess(command.getId(), command.getPassword())){
            // TODO: 댓글 삭제 로직을 서비스 계층으로 이동?
            commentService.deleteCommentsOfArticle(command.getId());
            articleService.deleteArticle(command.getId());
            return "redirect:/board/list";
        } else {
            throw new AuthenticationFailedException();
        }
    }
}
