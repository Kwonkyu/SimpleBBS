package com.haruhiism.bbs.service;

import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.exception.NoCommentFoundException;
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
    public Page<BoardArticle> readAllByWriterByPages(String writer, int pageNum, int pageSize) {
        return articleRepository.findAllByWriterContainingOrderByArticleIDDesc(writer, PageRequest.of(pageNum, pageSize));
    }

    @Override
    public Page<BoardArticle> readAllByTitleByPages(String title, int pageNum, int pageSize) {
        return articleRepository.findAllByTitleContainingOrderByArticleIDDesc(title, PageRequest.of(pageNum, pageSize));
    }

    @Override
    public Page<BoardArticle> readAllByContentByPages(String content, int pageNum, int pageSize) {
        return articleRepository.findAllByContentContainingOrderByArticleIDDesc(content, PageRequest.of(pageNum, pageSize));
    }

    @Override
    public Page<BoardArticle> readAllByTitleOrContentByPages(String keyword, int pageNum, int pageSize) {
        return articleRepository.findAllByTitleContainingOrContentContainingOrderByArticleIDDesc(keyword, keyword, PageRequest.of(pageNum, pageSize));
    }


    @Override
    @Transactional
    public void updateArticle(BoardArticle boardArticle) {
        if(articleRepository.existsById(boardArticle.getArticleID())){
            articleRepository.save(boardArticle);
        } else {
            throw new UpdateDeletedArticleException();
        }
    }


    @Override
    @Transactional
    public void deleteArticle(Long articleID){
        if(articleRepository.existsById(articleID)) {
            articleRepository.deleteByArticleID(articleID);
        } else {
            throw new NoArticleFoundException();
        }
    }

    @Override
    @Transactional
    public void deleteArticle(BoardArticle boardArticle) {
        articleRepository.delete(boardArticle);
    }


    @Override
    public boolean authArticleAccess(Long articleID, String rawPassword) {
        if(articleRepository.existsById(articleID)) {
            BoardArticle readArticle = readArticle(articleID);
            return dataEncoder.compare(rawPassword, readArticle.getPassword());
        } else {
            throw new NoArticleFoundException();
        }
    }



    @Override
    @Transactional
    public void createComment(BoardComment comment) {
//        } else {
//            BoardArticle article = commentedArticle.get();
//            article.getComments().add(comment);
//            articleRepository.save(article);
//        }
        if(articleRepository.existsById(comment.getArticleID())){
            comment.setPassword(dataEncoder.encode(comment.getPassword()));
            commentRepository.save(comment);
        } else {
            throw new NoArticleFoundException();
        }
    }

    @Override
    public BoardComment readComment(Long commentID) {
        Optional<BoardComment> comment = commentRepository.findById(commentID);
        if(comment.isEmpty()){
            throw new NoCommentFoundException();
        } else {
            return comment.get();
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
    public void deleteComment(Long commentID) {
        if(commentRepository.existsById(commentID)) {
            commentRepository.deleteById(commentID);
        } else {
            throw new NoCommentFoundException();
        }
    }

    @Override
    @Transactional
    public void deleteComment(BoardComment comment) {
        commentRepository.delete(comment);
    }

    @Override
    public boolean authCommentAccess(Long commentID, String rawPassword){
        if(commentRepository.existsById(commentID)) {
            BoardComment comment = readComment(commentID);
            return dataEncoder.compare(rawPassword, comment.getPassword());
        } else {
            throw new NoCommentFoundException();
        }
    }
}
