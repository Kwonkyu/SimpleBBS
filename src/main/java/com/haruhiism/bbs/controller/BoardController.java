package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.domain.BoardArticle;
import com.haruhiism.bbs.repository.BoardRepository;
import com.haruhiism.bbs.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/list")
    public String listBoardArticles(Model model){
        // TODO: apply pagination
        return "redirect:/not-implemented.html";
    }

    @GetMapping("/write")
    public String writeBoardArticle(){
        return "board/write";
    }

    @PostMapping("/write")
    public String submitBoardArticle(@RequestParam String writer,
                                     @RequestParam String title,
                                     @RequestParam String content){
        BoardArticle boardArticle = new BoardArticle();
        boardArticle.setWriter(writer);
        boardArticle.setTitle(title);
        boardArticle.setContent(content);
        boardService.create(boardArticle);
        return "redirect:/board/list";
    }
}
