package com.haruhiism.bbs.service.BoardService;

import com.haruhiism.bbs.domain.BoardArticle;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.exception.UpdateDeletedArticleException;
import com.haruhiism.bbs.repository.BoardRepository;
import com.haruhiism.bbs.service.DataEncoder.DataEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BasicBoardService implements BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private DataEncoder dataEncoder;


    @Override
    public void createArticle(BoardArticle article) {
        article.setPassword(dataEncoder.encode(article.getPassword()));
        boardRepository.save(article);
    }


    @Override
    public BoardArticle readArticle(Long articleID) {
        Optional<BoardArticle> readArticle = boardRepository.findById(articleID);
        if(readArticle.isEmpty()){
            throw new NoArticleFoundException();
        } else {
            return readArticle.get();
        }
    }

    @Override
    public List<BoardArticle> readAll() {
        List<BoardArticle> articles = new ArrayList<>();
        boardRepository.findAll().forEach(articles::add);
        return articles;
    }

    @Override
    public Page<BoardArticle> readAllByPages(int pageNum, int pageSize){
        return boardRepository.findAllByOrderByBidAsc(PageRequest.of(pageNum, pageSize));
    }


    @Override
    public void updateArticle(BoardArticle boardArticle) {
        if(boardRepository.findById(boardArticle.getBid()).isEmpty()){
            throw new UpdateDeletedArticleException();
        } else {
            boardRepository.save(boardArticle);
        }
    }


    @Override
    public void deleteArticle(Long bid){
        boardRepository.deleteByBid(bid);
    }

    @Override
    public void deleteArticle(BoardArticle boardArticle) {
        boardRepository.delete(boardArticle);
    }


    @Override
    public boolean authEntityAccess(Long bid, String rawPassword){
        BoardArticle readArticle = readArticle(bid);
        return dataEncoder.compare(rawPassword, readArticle.getPassword());
    }

}
