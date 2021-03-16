package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.domain.command.article.*;
import com.haruhiism.bbs.domain.entity.ArticleAndComments;
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
import org.springframework.web.bind.annotation.*;

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


    @GetMapping("/")
    public String redirectToList(){
        return "redirect:/board/list";
    }

    @GetMapping("/list")
    public String listBoardArticles(Model model,
                                    @Valid ArticleListCommand command,
                                    BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "redirect:/board/list";
        }

        Page<BoardArticle> articles = articleService.readAllByPages(command.getPageNum(), command.getPageSize());
        // int[] articleCommentSizes = articles.get().mapToInt(boardArticle -> commentService.readAll(boardArticle.getArticleID()).size()).toArray();

        List<ArticleAndComments> articleAndComments = new LinkedList<>();
        articles.get().forEachOrdered(boardArticle -> articleAndComments.add(new ArticleAndComments(boardArticle, commentService.readCommentsOfArticle(boardArticle.getArticleID()).size())));

        model.addAttribute("articleAndComments", articleAndComments);
        model.addAttribute("currentPage", articles.getNumber());
        model.addAttribute("pages", articles.getTotalPages());
        return "board/list";
    }


    @GetMapping("/read")
    public String readBoardArticle(Model model, @RequestParam long id) {
        // TODO: comment paging.
        BoardArticle article = articleService.readArticle(id);
        ArticleAndComments articleAndComments = new ArticleAndComments(article, commentService.readCommentsOfArticle(id));
        model.addAttribute("articleAndComments", articleAndComments);
        return "board/read";
    }


    @GetMapping("/write")
    public String writeBoardArticle(@ModelAttribute("command") ArticleSubmitCommand command){
        return "board/write";
    }

    @PostMapping("/write")
    // @ModelAttribute automatically add annotated object to model. https://developer-joe.tistory.com/197
    public String submitBoardArticle(@ModelAttribute("command") @Valid ArticleSubmitCommand command,
                                     BindingResult bindingResult, HttpServletResponse response){
        if(bindingResult.hasErrors()){
            // now write.html takes error object as model.
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
    public String requestEditArticle(Model model, @RequestParam("id") Long articleID){
        model.addAttribute("articleID", articleID);
        return "board/editRequest";
    }

    @PostMapping("/edit")
    public String authEditArticle(Model model, @ModelAttribute("command") ArticleEditAuthCommand command){
        if(articleService.authArticleAccess(command.getArticleID(), command.getPassword())){
            BoardArticle readArticle = articleService.readArticle(command.getArticleID());
            command.setWriter(readArticle.getWriter());
            command.setTitle(readArticle.getTitle());
            command.setContent(readArticle.getContent());
            return "board/edit";
        } else {
            throw new ArticleAuthFailedException();
        }
    }


    @PostMapping("/edit/submit")
    public String submitEditArticle(ArticleEditAuthCommand command){
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
    public String requestRemoveArticle(Model model, @RequestParam("id") Long articleID){
        model.addAttribute("articleID", articleID);
        return "board/removeRequest";
    }

    @PostMapping("/remove")
    public String authRemoveArticle(@Valid ArticleRemoveRequestCommand command, BindingResult bindingResult){
        if(bindingResult.hasErrors()) {
            return "redirect:/board/list";
        }

        if(articleService.authArticleAccess(command.getArticleID(), command.getPassword())){
            articleService.deleteArticle(command.getArticleID());
            return "redirect:/board/list";
        } else {
            throw new ArticleAuthFailedException();
        }
    }
}
