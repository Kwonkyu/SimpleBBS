package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.domain.*;
import com.haruhiism.bbs.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/board")
public class BoardController {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
        Page<BoardArticle> articles = boardService.readAll(command.getPageNum(), command.getPageSize());
        model.addAttribute("articles", articles);
        model.addAttribute("currentPage", articles.getNumber());
        model.addAttribute("pages", articles.getTotalPages());
        return "board/list";
    }

    @GetMapping("/read")
    public String readBoardArticle(Model model, @RequestParam long bid) {
        BoardArticle article = boardService.read(bid);
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
                                     BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            // now write.html takes error object as model.
            return "board/write";
        }
        BoardArticle boardArticle = new BoardArticle(command.getWriter(), passwordEncoder.encode(command.getPassword()), command.getTitle(), command.getContent());
        boardService.create(boardArticle);
        return "redirect:/board/list";
    }

    @GetMapping("/edit")
    public String requestEditArticle(@ModelAttribute("command") BoardEditRequestCommand command){
        return "board/editRequest";
    }

    @PostMapping("/edit")
    public String authEditArticle(Model model, @ModelAttribute("command") BoardEditAuthCommand command){
        BoardArticle readArticle = boardService.read(command.getBid());
        if(passwordEncoder.matches(command.getPassword(), readArticle.getPassword())){
            command.setWriter(readArticle.getWriter());
            command.setTitle(readArticle.getTitle());
            command.setContent(readArticle.getContent());
            return "board/edit";
        } else {
            return "redirect:/board/list";
        }
    }

    @PostMapping("/edit/submit")
    public String submitEditArticle(BoardEditAuthCommand command){
        BoardArticle editArticle = boardService.read(command.getBid());
        if(passwordEncoder.matches(command.getPassword(), editArticle.getPassword())){
            editArticle.setTitle(command.getTitle());
            editArticle.setContent(command.getContent());
            boardService.update(editArticle);
        }

        return "redirect:/board/list";
    }
}
