package com.haruhiism.bbs.service;

import com.haruhiism.bbs.domain.SearchMode;
import com.haruhiism.bbs.domain.dto.BoardArticleAuthDTO;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.exception.AuthenticationFailedException;
import com.haruhiism.bbs.exception.NoAccountFoundException;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.exception.UpdateDeletedArticleException;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.repository.CommentRepository;
import com.haruhiism.bbs.service.DataEncoder.DataEncoder;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.authentication.LoginSessionInfo;
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
    private final AccountRepository accountRepository;

    private final DataEncoder dataEncoder;


    @Override
    public void createArticle(BoardArticleDTO article, LoginSessionInfo loginSessionInfo) {
        BoardArticle boardArticle = new BoardArticle(
                article.getWriter(),
                dataEncoder.encode(article.getPassword()),
                article.getTitle(),
                article.getContent());

        if(loginSessionInfo != null){
            boardArticle.changeWriter(loginSessionInfo.getUsername());

            boardArticle.registerAccountInfo(
                    accountRepository.findById(loginSessionInfo.getAccountID())
                            .orElseThrow(NoAccountFoundException::new));
        }

        articleRepository.save(boardArticle);
    }


    @Override
    @Transactional(readOnly = true)
    public BoardArticleDTO readArticle(Long articleID) {
        Optional<BoardArticle> readArticle = articleRepository.findById(articleID);
        if(readArticle.isEmpty()){
            throw new NoArticleFoundException();
        } else {
            return new BoardArticleDTO(readArticle.get());
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
            articles.add(new BoardArticleDTO(boardArticle));
            commentSizes.add(commentRepository.countAllByBoardArticle(boardArticle));
        });

        return new BoardArticlesDTO(articles, commentSizes, currentPage, result.getTotalPages());
    }


    private boolean verifyArticleAndAccount(BoardArticle boardArticle, String authValue, LoginSessionInfo loginSessionInfo){
        Optional<Long> articleWriter = getArticleWriterId(boardArticle);
        if(articleWriter.isPresent()){
            return loginSessionInfo != null && articleWriter.get().equals(loginSessionInfo.getAccountID());
        } else {
            return dataEncoder.compare(authValue, boardArticle.getPassword());
        }
    }


    @Override
    public void updateArticle(BoardArticleDTO article, LoginSessionInfo loginSessionInfo) {
        BoardArticle updatedArticle = articleRepository.findById(article.getId())
                .orElseThrow(UpdateDeletedArticleException::new);

        if(verifyArticleAndAccount(updatedArticle, article.getPassword(), loginSessionInfo)){
            updatedArticle.changeTitle(article.getTitle());
            updatedArticle.changeContent(article.getContent());
        } else {
            throw new AuthenticationFailedException();
        }
    }


    @Override
    public void deleteArticle(BoardArticleAuthDTO authDTO, LoginSessionInfo loginSessionInfo){
        BoardArticle deletedArticle = articleRepository.findById(authDTO.getArticleId())
                .orElseThrow(NoArticleFoundException::new);

        if(verifyArticleAndAccount(deletedArticle, authDTO.getRawPassword(), loginSessionInfo)){
            commentRepository.deleteAllByBoardArticle(deletedArticle);
            articleRepository.delete(deletedArticle);
        } else {
            throw new AuthenticationFailedException();
        }
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<BoardArticleDTO> authArticleAccess(BoardArticleAuthDTO authDTO) {
        BoardArticle article = articleRepository.findById(authDTO.getArticleId())
                .orElseThrow(NoArticleFoundException::new);

        if(verifyArticleAndAccount(article, authDTO.getRawPassword(), authDTO.getLoginSessionInfo())){
            BoardArticleDTO boardArticleDTO = new BoardArticleDTO(article);
            boardArticleDTO.setPassword(authDTO.getRawPassword() == null ? "PASSWORD" : authDTO.getRawPassword());
            return Optional.of(boardArticleDTO);
        } else {
            return Optional.empty();
        }
    }


    private Optional<Long> getArticleWriterId(BoardArticle boardArticle) {
        if(boardArticle.getBoardAccount() == null) return Optional.empty();
        return Optional.of(boardArticle.getBoardAccount().getId());
    }
}
