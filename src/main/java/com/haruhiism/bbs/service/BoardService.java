package com.haruhiism.bbs.service;

import com.haruhiism.bbs.domain.BoardArticle;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.repository.BoardRepository;
import javafx.scene.control.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    public void create(BoardArticle article) {
        boardRepository.save(article);
    }

    public BoardArticle read(BoardArticle article){
        return read(article.getBid());
    }

    public BoardArticle read(Long bid){
        Optional<BoardArticle> readArticle = boardRepository.findById(bid);
        if(readArticle.isEmpty()){
            throw new NoArticleFoundException();
        } else {
            return readArticle.get();
        }

    }

    public Page<BoardArticle> readAll(int pageNum, int pageSize){
        return boardRepository.findAllByOrderByBidAsc(PageRequest.of(pageNum, pageSize));
    }

    public void update(BoardArticle article){
        boardRepository.save(article);
    }

    public void delete(BoardArticle article){
        boardRepository.delete(article);
    }
}
