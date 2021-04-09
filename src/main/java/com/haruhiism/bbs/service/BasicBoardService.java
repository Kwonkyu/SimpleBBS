package com.haruhiism.bbs.service;

import com.haruhiism.bbs.domain.SearchMode;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.exception.UpdateDeletedArticleException;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.repository.CommentRepository;
import com.haruhiism.bbs.service.DataEncoder.DataEncoder;
import com.haruhiism.bbs.service.article.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicBoardService implements ArticleService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    private final DataEncoder dataEncoder;


    @Override
    public void createArticle(BoardArticleDTO...articles) {
        for(BoardArticleDTO article: articles){
            articleRepository.save(new BoardArticle(
                    article.getWriter(),
                    dataEncoder.encode(article.getPassword()),
                    article.getTitle(),
                    article.getContent())
            );
        }
    }


    @Override
    @Transactional(readOnly = true)
    public BoardArticleDTO readArticle(Long articleID) {
        Optional<BoardArticle> readArticle = articleRepository.findById(articleID);
        if(readArticle.isEmpty()){
            throw new NoArticleFoundException();
        } else {
            BoardArticle article = readArticle.get();
            return new BoardArticleDTO(
                    articleID,
                    article.getWriter(),
                    article.getPassword(),
                    article.getTitle(),
                    article.getContent());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BoardArticlesDTO readAllByPages(int pageNum, int pageSize){
        Page<BoardArticle> boardArticles = articleRepository.findAllByOrderByIdDesc(PageRequest.of(pageNum, pageSize));
        return convertPageResultToBoardArticlesDTO(boardArticles, pageNum);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardArticlesDTO searchAllByPages(SearchMode searchMode, String keyword, int pageNum, int pageSize) {
        Page<BoardArticle> result = null;

        switch(searchMode){
            case TITLE:
                result = articleRepository.findAllByTitleContainingOrderByIdDesc(keyword, PageRequest.of(pageNum, pageSize));
                break;

            case WRITER:
                result = articleRepository.findAllByWriterContainingOrderByIdDesc(keyword, PageRequest.of(pageNum, pageSize));
                break;

            case CONTENT:
                result = articleRepository.findAllByContentContainingOrderByIdDesc(keyword, PageRequest.of(pageNum, pageSize));
                break;

            case TITLE_CONTENT:
                result = articleRepository.findAllByTitleContainingOrContentContainingOrderByIdDesc(keyword, keyword, PageRequest.of(pageNum, pageSize));
                break;
        }

        return convertPageResultToBoardArticlesDTO(result, pageNum);
    }


    private BoardArticlesDTO convertPageResultToBoardArticlesDTO(Page<BoardArticle> result, int currentPage){
        List<BoardArticleDTO> articles = new ArrayList<>();
        List<Integer> commentSizes = new ArrayList<>();

        result.get().forEachOrdered(boardArticle -> {
            BoardArticleDTO boardArticleDTO = new BoardArticleDTO(
                    boardArticle.getId(),
                    boardArticle.getWriter(),
                    boardArticle.getPassword(),
                    boardArticle.getTitle(),
                    boardArticle.getContent());

            boardArticleDTO.setId(boardArticleDTO.getId());
            articles.add(boardArticleDTO);

            commentSizes.add(commentRepository.countAllByBoardArticle(boardArticle));
        });

        return new BoardArticlesDTO(articles, commentSizes, currentPage, result.getTotalPages());
    }


    @Override
    public void updateArticle(BoardArticleDTO boardArticleDTO) {
        // TODO: 인증 로직을 컨트롤러에서 서비스 메서드 내부로 이동?
        Optional<BoardArticle> updatedArticle = articleRepository.findById(boardArticleDTO.getId());
        if(updatedArticle.isEmpty()){
            throw new UpdateDeletedArticleException();
        }

        BoardArticle boardArticle = updatedArticle.get();
        boardArticle.changeTitle(boardArticleDTO.getTitle());
        boardArticle.changeContent(boardArticleDTO.getContent());
//        articleRepository.save(boardArticle);
    }


    @Override
    public void deleteArticle(Long articleID){
        Optional<BoardArticle> deletedArticle = articleRepository.findById(articleID);
        if(deletedArticle.isEmpty()){
            throw new NoArticleFoundException();
        }

        articleRepository.delete(deletedArticle.get());
    }


    @Override
    @Transactional(readOnly = true)
    public boolean authArticleAccess(Long articleID, String rawPassword) {
        Optional<BoardArticle> readArticle = articleRepository.findById(articleID);
        if (readArticle.isEmpty()) {
            throw new NoArticleFoundException();
        }

        return dataEncoder.compare(rawPassword, readArticle.get().getPassword());
    }

}
