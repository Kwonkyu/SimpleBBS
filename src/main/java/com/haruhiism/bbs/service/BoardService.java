package com.haruhiism.bbs.service;

import com.haruhiism.bbs.domain.BoardArticle;
import com.haruhiism.bbs.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    public void create(BoardArticle article) {
        boardRepository.save(article);
    }

    public void read(BoardArticle article){

    }

    public void update(BoardArticle article){

    }

    public void delete(BoardArticle article){

    }
}
