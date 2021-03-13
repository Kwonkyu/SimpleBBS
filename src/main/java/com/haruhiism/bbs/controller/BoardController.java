package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.domain.*;
import com.haruhiism.bbs.exception.ArticleEditAuthFailedException;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.exception.UpdateDeletedArticleException;
import com.haruhiism.bbs.service.BoardService.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/")
    public String redirectToList(){
        return "redirect:/board/list";
    }

    @GetMapping("/list")
    public String listBoardArticles(Model model,
                                    @Valid BoardListCommand command,
                                    BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "redirect:/board/list";
        }
        Page<BoardArticle> articles = boardService.readAllByPages(command.getPageNum(), command.getPageSize());
        model.addAttribute("articles", articles);
        model.addAttribute("currentPage", articles.getNumber());
        model.addAttribute("pages", articles.getTotalPages());
        return "board/list";
    }


    @GetMapping("/read")
    public String readBoardArticle(Model model, @RequestParam long bid) {
        BoardArticle article = boardService.readArticle(bid);
        model.addAttribute("article", article);
        return "board/read";
    }


    @GetMapping("/write")
    public String writeBoardArticle(@ModelAttribute("command") BoardSubmitCommand command){
        return "board/write";
    }

    @PostMapping("/write")
    // @ModelAttribute automatically add annotated object to model. https://developer-joe.tistory.com/197
    public String submitBoardArticle(@ModelAttribute("command") @Valid BoardSubmitCommand command,
                                     BindingResult bindingResult, HttpServletResponse response){
        if(bindingResult.hasErrors()){
            // now write.html takes error object as model.
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
            return "board/write";
        }

        boardService.createArticle(new BoardArticle(
                command.getWriter(),
                command.getPassword(),
                command.getTitle(),
                command.getContent())
        );
        return "redirect:/board/list";
    }


    @GetMapping("/edit")
    public String requestEditArticle(@ModelAttribute("command") BoardEditRequestCommand command){
        return "board/editRequest";
    }

    @PostMapping("/edit")
    public String authEditArticle(Model model, @ModelAttribute("command") BoardEditAuthCommand command){
        if(boardService.authEntityAccess(command.getBid(), command.getPassword())){
            BoardArticle readArticle = boardService.readArticle(command.getBid());
            command.setWriter(readArticle.getWriter());
            command.setTitle(readArticle.getTitle());
            command.setContent(readArticle.getContent());
            return "board/edit";
        } else {
            throw new ArticleEditAuthFailedException();
        }
    }


    @PostMapping("/edit/submit")
    public String submitEditArticle(BoardEditAuthCommand command){
        if(boardService.authEntityAccess(command.getBid(), command.getPassword())) {
            try {
                BoardArticle editArticle = boardService.readArticle(command.getBid());
                editArticle.setTitle(command.getTitle());
                editArticle.setContent(command.getContent());
                boardService.updateArticle(editArticle);
            } catch (NoArticleFoundException e){
                throw new UpdateDeletedArticleException();
            }
        } else {
            throw new ArticleEditAuthFailedException();
        }

        return "redirect:/board/list";
    }
}
