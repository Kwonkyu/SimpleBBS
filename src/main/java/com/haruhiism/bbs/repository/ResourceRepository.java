package com.haruhiism.bbs.repository;

import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.UploadedFile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends CrudRepository<UploadedFile, Long> {

    List<UploadedFile> findAllByBoardArticleOrderByIdAsc(BoardArticle boardArticle);

    Optional<UploadedFile> findByHashedFilename(String hashedFilename);

    void deleteByHashedFilename(String hashedFilename);
}
