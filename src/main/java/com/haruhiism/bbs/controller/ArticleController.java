package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.domain.command.article.*;
import com.haruhiism.bbs.domain.ArticleAndComments;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.exception.ArticleAuthFailedException;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.exception.UpdateDeletedArticleException;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.comment.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/board")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentService commentService;

    // TODO: pageSize option.
    @GetMapping("/list")
    public String listBoardArticles(Model model, @ModelAttribute("command") @Valid ArticleListCommand command){
        Page<BoardArticle> articles = articleService.readAllByPages(command.getPageNum(), command.getPageSize());
        List<ArticleAndComments> articleAndComments = new LinkedList<>();
        articles.get().forEachOrdered(boardArticle ->
                articleAndComments.add(
                        new ArticleAndComments(
                                boardArticle,
                                commentService.readCommentsOfArticle(boardArticle.getArticleID()).size()
                        )
                )
        );

        model.addAttribute("articleAndComments", articleAndComments);
        model.addAttribute("currentPage", articles.getNumber());
        model.addAttribute("pages", articles.getTotalPages());
        return "board/list";
    }


    @GetMapping("/search")
    public String searchArticles(Model model, @ModelAttribute("command") @Valid ArticleSearchCommand command) {
        Page<BoardArticle> articles = null;
        switch(command.getMode()) {
            case WRITER:
                articles = articleService.readAllByWriterByPages(command.getKeyword(), command.getPageNum(), command.getPageSize());
                break;

            case TITLE:
                articles = articleService.readAllByTitleByPages(command.getKeyword(), command.getPageNum(), command.getPageSize());
                break;

            case CONTENT:
                articles = articleService.readAllByContentByPages(command.getKeyword(), command.getPageNum(), command.getPageSize());
                break;

            case TITLE_CONTENT:
                articles = articleService.readAllByTitleOrContentByPages(command.getKeyword(), command.getPageNum(), command.getPageSize());
                break;
        }
        List<ArticleAndComments> articleAndComments = new LinkedList<>();
        articles.get().forEachOrdered(boardArticle ->
                articleAndComments.add(
                        new ArticleAndComments(
                                boardArticle,
                                commentService.readCommentsOfArticle(boardArticle.getArticleID()).size()
                        )
                )
        );

        model.addAttribute("articleAndComments", articleAndComments);
        model.addAttribute("currentPage", articles.getNumber());
        model.addAttribute("pages", articles.getTotalPages());
        return "board/search";
    }


    @GetMapping("/read")
    public String readBoardArticle(Model model, @Valid ArticleReadCommand command) {
        // TODO: comment paging.
        BoardArticle article = articleService.readArticle(command.getId());
        ArticleAndComments articleAndComments = new ArticleAndComments(article, commentService.readCommentsOfArticle(command.getId()));
        model.addAttribute("articleAndComments", articleAndComments);
        return "board/read";
    }


    @GetMapping("/write")
    // no validation here because it's writing a new article.
    public String writeBoardArticle(@ModelAttribute("command") ArticleSubmitCommand command){
        return "board/write";
    }

    @PostMapping("/write")
    // @ModelAttribute automatically add annotated object to model. https://developer-joe.tistory.com/197
    public String submitBoardArticle(@ModelAttribute("command") @Valid ArticleSubmitCommand command, BindingResult bindingResult, HttpServletResponse response){
        if(bindingResult.hasErrors()){
            // It has more priority than controller advice's exception handler.
            // Validation should not be handled by exception handlers because of user feedback.
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
            return "board/write";
        }

        articleService.createArticle(new BoardArticle(
                command.getWriter(),
                command.getPassword(),
                command.getTitle(),
                command.getContent())
        );
        return "redirect:/board/list";
    }


    @GetMapping("/edit")
    public String requestEditArticle(Model model, @Valid ArticleEditRequestCommand command){
        model.addAttribute("articleID", command.getId());
        return "board/editRequest";
    }

    @PostMapping("/edit")
    public String authEditArticle(Model model, @ModelAttribute("command") @Valid ArticleEditAuthCommand command){
        if(articleService.authArticleAccess(command.getArticleID(), command.getPassword())){
            BoardArticle readArticle = articleService.readArticle(command.getArticleID());
            BoardArticle editArticle = new BoardArticle(
                    readArticle.getWriter(),
                    command.getPassword(),
                    readArticle.getTitle(),
                    readArticle.getContent());
            editArticle.setArticleID(command.getArticleID());
            model.addAttribute("article", editArticle);
            return "board/edit";
        } else {
            throw new ArticleAuthFailedException();
        }
    }


    @PostMapping("/edit/submit")
    public String submitEditArticle(@Valid ArticleEditSubmitCommand command){
        if(articleService.authArticleAccess(command.getArticleID(), command.getPassword())) {
            try {
                BoardArticle editArticle = articleService.readArticle(command.getArticleID());
                editArticle.setTitle(command.getTitle());
                editArticle.setContent(command.getContent());
                articleService.updateArticle(editArticle);
            } catch (NoArticleFoundException e){
                throw new UpdateDeletedArticleException();
            }
        } else {
            throw new ArticleAuthFailedException();
        }

        return "redirect:/board/list";
    }


    @GetMapping("/remove")
    public String requestRemoveArticle(Model model, @Valid ArticleRemoveRequestCommand command){
        model.addAttribute("articleID", command.getId());
        return "board/removeRequest";
    }

    @PostMapping("/remove")
    public String authRemoveArticle(@Valid ArticleRemoveAuthCommand command){
        if(articleService.authArticleAccess(command.getArticleID(), command.getPassword())){
            articleService.deleteArticle(command.getArticleID());
            return "redirect:/board/list";
        } else {
            throw new ArticleAuthFailedException();
        }
    }
}
