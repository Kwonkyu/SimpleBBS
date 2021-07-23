package com.haruhiism.bbs.service.article;

import com.haruhiism.bbs.domain.ArticleSearchMode;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.exception.article.NoArticleFoundException;
import com.haruhiism.bbs.exception.article.UpdateDeletedArticleException;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.haruhiism.bbs.service.RepositoryUtility.findAccountByUserId;
import static com.haruhiism.bbs.service.RepositoryUtility.findArticleById;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicArticleService implements ArticleService {

    private final ArticleRepository articleRepository;
    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    public boolean authorizeArticleAccess(long articleId, String password) {
        return passwordEncoder.matches(password, findArticleById(articleRepository, articleId).getPassword());
    }

    @Override
    public long createArticle(BoardArticleDTO article) {
        article.encodePassword(passwordEncoder);
        BoardArticle boardArticle = new BoardArticle(article);
        articleRepository.save(boardArticle);
        return boardArticle.getId();
    }

    @Override
    public long createArticle(BoardArticleDTO article, String userId) {
        article.encodePassword(passwordEncoder, UUID.randomUUID().toString());
        BoardAccount boardAccount = findAccountByUserId(accountRepository, userId);
        BoardArticle boardArticle = new BoardArticle(article, boardAccount);
        articleRepository.save(boardArticle);
        return boardArticle.getId();
    }

    @Override
    public BoardArticleDTO readArticle(long articleId) { // not readonly because view counts need to be updated.
        BoardArticle article = findArticleById(articleRepository, articleId);
        if(article.isDeleted()){
            throw new NoArticleFoundException();
        } else {
            article.increaseHit();
            return new BoardArticleDTO(article);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BoardArticleDTO.PagedArticles readAllByPages(int pageNum, int pageSize){
        Page<BoardArticle> articles = articleRepository.findAllByDeletedFalseOrderByIdDesc(PageRequest.of(pageNum, pageSize));
        return new BoardArticleDTO.PagedArticles(articles);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardArticleDTO.PagedArticles searchAllByPages(ArticleSearchMode articleSearchMode, String keyword, int pageNum, int pageSize) {
        Page<BoardArticle> result = Page.empty();
        PageRequest page = PageRequest.of(pageNum, pageSize);

        switch(articleSearchMode){
            case TITLE:
                result = articleRepository.findAllByTitleContainingAndDeletedFalseOrderByIdDesc(keyword, page);
                break;

            case WRITER:
                result = articleRepository.findAllByWriterContainingAndDeletedFalseOrderByIdDesc(keyword, page);
                break;

            case CONTENT:
                result = articleRepository.findAllByContentContainingAndDeletedFalseOrderByIdDesc(keyword, page);
                break;

            case TITLE_CONTENT:
                result = articleRepository.findAllByTitleContainingOrContentContainingOrderByIdDesc(keyword, keyword, page);
                break;

            case ACCOUNT:
                BoardAccount account = findAccountByUserId(accountRepository, keyword);
                result = articleRepository.findAllByBoardAccountAndDeletedFalse(account, page);
        }

        return new BoardArticleDTO.PagedArticles(result);
    }

    @Override
    public void updateArticle(BoardArticleDTO article) {
        BoardArticle updatedArticle = articleRepository.findById(article.getId())
                .orElseThrow(UpdateDeletedArticleException::new);

        if (updatedArticle.isDeleted()) {
            throw new UpdateDeletedArticleException();
        }

        updatedArticle.changeTitle(article.getTitle());
        updatedArticle.changeContent(article.getContent());
    }


    @Override
    public void deleteArticle(long articleId){
        BoardArticle deletedArticle = findArticleById(articleRepository, articleId);
        deletedArticle.delete();
    }
}
