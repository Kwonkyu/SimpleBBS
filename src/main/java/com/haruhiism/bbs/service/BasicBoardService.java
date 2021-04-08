package com.haruhiism.bbs.service;

import com.haruhiism.bbs.domain.SearchMode;
import com.haruhiism.bbs.domain.dto.BoardArticleDTO;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentDTO;
import com.haruhiism.bbs.domain.dto.BoardCommentsDTO;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.BoardComment;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.exception.NoCommentFoundException;
import com.haruhiism.bbs.exception.UpdateDeletedArticleException;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.repository.CommentRepository;
import com.haruhiism.bbs.service.DataEncoder.DataEncoder;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicBoardService implements ArticleService, CommentService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final AccountRepository accountRepository;

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

            // TODO: 전체 갯수를 반환하는게 맞나?
            commentSizes.add(readRawCommentsOfArticle(boardArticle.getId(), 0, 1).getNumberOfElements());
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



    @Override
    public void createComment(BoardCommentDTO comment) {
        Optional<BoardArticle> article = articleRepository.findById(comment.getArticleID());
        BoardArticle commentedArticle = article.orElseThrow(NoArticleFoundException::new);

        BoardComment boardComment = new BoardComment(
                comment.getWriter(),
                dataEncoder.encode(comment.getPassword()),
                comment.getContent(),
                commentedArticle);

        // TODO: use optional?
        if(comment.getAccountID() != null){
            Optional<BoardAccount> account = accountRepository.findById(comment.getAccountID());
            account.ifPresent(boardComment::setCommentWriter);
        }

        commentRepository.save(boardComment);
    }

    private Page<BoardComment> readRawCommentsOfArticle(Long articleID, int pageNum, int pageSize){
        return commentRepository.findAllByBoardArticleOrderByIdAsc(
                articleRepository.findById(articleID)
                        .orElseThrow(NoArticleFoundException::new),
                PageRequest.of(pageNum, pageSize));
    }


    @Override
    @Transactional(readOnly = true)
    public BoardCommentsDTO readCommentsOfArticle(Long articleID, int pageNum, int pageSize){
        Page<BoardComment> boardComments = readRawCommentsOfArticle(articleID, pageNum, pageSize);
        List<BoardCommentDTO> comments = boardComments.get().map(
                boardComment -> {
                    BoardCommentDTO boardCommentDTO = new BoardCommentDTO(
                            boardComment.getBoardArticle().getId(),
                            boardComment.getWriter(),
                            boardComment.getPassword(),
                            boardComment.getContent());

                    boardCommentDTO.setCommentId(boardComment.getId());
                    return boardCommentDTO;
                })
                .collect(Collectors.toList());

        return new BoardCommentsDTO(comments, pageNum, boardComments.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public BoardCommentDTO readComment(Long commentID) {
        Optional<BoardComment> boardComment = commentRepository.findById(commentID);
        if(boardComment.isEmpty()){
            throw new NoCommentFoundException();
        }

        // TODO: DTO의 생성자로 Entity 객체들을 전달해서 생성하는 방법으로 변경?
        BoardComment comment = boardComment.get();
        return new BoardCommentDTO(
                commentID,
                comment.getWriter(),
                comment.getPassword(),
                comment.getContent());
    }

    @Override
    public void updateComment(BoardCommentDTO comment) {

    }

    @Override
    public void deleteComment(Long commentID) {
        if(commentRepository.existsById(commentID)) {
            commentRepository.deleteById(commentID);
        } else {
            throw new NoCommentFoundException();
        }
    }

    @Override
    public void deleteCommentsOfArticle(Long articleID) {
        commentRepository.deleteAllByBoardArticle(articleRepository.findById(articleID).orElseThrow(NoArticleFoundException::new));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authCommentAccess(Long commentID, String rawPassword){
        Optional<BoardComment> comment = commentRepository.findById(commentID);
        if (comment.isEmpty()) {
            throw new NoCommentFoundException();
        }

        return dataEncoder.compare(rawPassword, comment.get().getPassword());
    }
}
