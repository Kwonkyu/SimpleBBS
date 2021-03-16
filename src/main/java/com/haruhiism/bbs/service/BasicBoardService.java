package com.haruhiism.bbs.service;

import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.exception.UpdateDeletedArticleException;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.repository.CommentRepository;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.DataEncoder.DataEncoder;
import com.haruhiism.bbs.service.comment.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BasicBoardService implements ArticleService, CommentService {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private DataEncoder dataEncoder;


    @Override
    @Transactional
    public void createArticle(BoardArticle article) {
        article.setPassword(dataEncoder.encode(article.getPassword()));
        articleRepository.save(article);
    }


    @Override
    public BoardArticle readArticle(Long articleID) {
        Optional<BoardArticle> readArticle = articleRepository.findById(articleID);
        if(readArticle.isEmpty()){
            throw new NoArticleFoundException();
        } else {
            return readArticle.get();
        }
    }


    @Override
    public Page<BoardArticle> readAllByPages(int pageNum, int pageSize){
        return articleRepository.findAllByOrderByArticleIDDesc(PageRequest.of(pageNum, pageSize));
    }


    @Override
    @Transactional
    public void updateArticle(BoardArticle boardArticle) {
        if(articleRepository.findById(boardArticle.getArticleID()).isEmpty()){
            throw new UpdateDeletedArticleException();
        } else {
            articleRepository.save(boardArticle);
        }
    }


    @Override
    @Transactional
    public void deleteArticle(Long articleID){
        articleRepository.deleteByArticleID(articleID);
    }

    @Override
    @Transactional
    public void deleteArticle(BoardArticle boardArticle) {
        articleRepository.delete(boardArticle);
    }


    @Override
    public boolean authEntityAccess(Long articleID, String rawPassword){
        BoardArticle readArticle = readArticle(articleID);
        return dataEncoder.compare(rawPassword, readArticle.getPassword());
    }


    @Override
    @Transactional
    public void createComment(BoardComment comment) {
//        Optional<BoardArticle> commentedArticle = articleRepository.findById(comment.getArticleID());
//        if(commentedArticle.isEmpty()){
//            throw new NoArticleFoundException();
//        } else {
//            BoardArticle article = commentedArticle.get();
//            article.getComments().add(comment);
//            articleRepository.save(article);
//        }
        Optional<BoardArticle> commentedArticle = articleRepository.findById(comment.getArticleID());
        if(commentedArticle.isEmpty()){
            throw new NoArticleFoundException();
        } else {
            comment.setPassword(dataEncoder.encode(comment.getPassword()));
            commentRepository.save(comment);
        }
    }

    @Override
    public List<BoardComment> readCommentsOfArticle(Long articleID){
        return commentRepository.findAllByArticleID(articleID);
    }

    @Override
    public Page<BoardComment> readCommentsOfArticleByPages(Long articleID, int pageNum, int pageSize) {
        return commentRepository.findByArticleIDOrderByCommentID(articleID, PageRequest.of(pageNum, pageSize));
    }

    @Override
    @Transactional
    public void updateComment(BoardComment comment) {

    }

    @Override
    @Transactional
    public void deleteAllCommentsOfArticle(Long articleID) {

    }

    @Override
    @Transactional
    public void deleteComment(Long commentID) {

    }

    @Override
    @Transactional
    public void deleteComment(BoardComment comment) {

    }
}
