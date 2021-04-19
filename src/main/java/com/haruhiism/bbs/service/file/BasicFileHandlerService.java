package com.haruhiism.bbs.service.file;

import com.haruhiism.bbs.domain.dto.ResourceDTO;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.UploadedFile;
import com.haruhiism.bbs.exception.InvalidFileException;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.repository.ResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Transactional
@Slf4j
public class BasicFileHandlerService implements FileHandlerService {

    private final Path uploadedFilePath = Paths.get("C:\\Temp\\SimpleBBS\\uploads");
    private final FileValidator fileValidator;
    private final ResourceRepository resourceRepository;
    private final ArticleRepository articleRepository;

    public BasicFileHandlerService(FileValidator fileValidator,
                                   ResourceRepository resourceRepository,
                                   ArticleRepository articleRepository) {
        this.fileValidator = fileValidator;
        this.resourceRepository = resourceRepository;
        this.articleRepository = articleRepository;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(uploadedFilePath);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create file directories.");
        }
    }

    private Path transferToValidFile(MultipartFile validatedFile, String filename) throws IOException {
        Path transferredFile = Paths.get(uploadedFilePath.toString(), filename);
        validatedFile.transferTo(transferredFile);
        return transferredFile;
    }

    public void store(MultipartFile uploadedFile, BoardArticle article) {
        try {
            log.info("uploading file {} / {} for article #{}", uploadedFile.getOriginalFilename(), uploadedFile.getSize(), article.getId());
            // hash filename so that files of duplicated won't be overwritten.
            String hashedValidFilename = fileValidator.validate(uploadedFile).orElseThrow(InvalidFileException::new);

            log.info("transferring file {} as {}", uploadedFile.getOriginalFilename(), hashedValidFilename);
            Path validFile = transferToValidFile(uploadedFile, hashedValidFilename);

            log.info("successfully transferred file {} as {}", uploadedFile.getOriginalFilename(), validFile.toAbsolutePath().toString());
            resourceRepository.save(new UploadedFile(Objects.requireNonNull(uploadedFile.getOriginalFilename()), hashedValidFilename, article));
        } catch (IOException e) {
            log.error("uploading file {} failed({}).", uploadedFile.getOriginalFilename(), e.getLocalizedMessage());
        }
    }

    @Override
    public void store(List<MultipartFile> files, Long articleId) {
        BoardArticle article = articleRepository.findById(articleId).orElseThrow(NoArticleFoundException::new);
        for (MultipartFile file : files) {
            store(file, article);
        }
    }

    @Override
    public ResourceDTO load(String hashedFilename) {
        Path actualFile = uploadedFilePath.resolve(hashedFilename);
        if(!Files.exists(actualFile)){
            throw new InvalidFileException();
        }

        String originalFilename = resourceRepository.findByHashedFilename(hashedFilename)
                .orElseThrow(InvalidFileException::new)
                .getFilename();

        return ResourceDTO.builder()
                .file(actualFile)
                .filename(originalFilename)
                .hashedFilename(hashedFilename).build();
    }

    @Override
    public List<ResourceDTO> listResourcesOfArticle(Long articleId) {
        BoardArticle boardArticle = articleRepository.findById(articleId).orElseThrow(NoArticleFoundException::new);
        List<UploadedFile> uploadedFiles = resourceRepository.findAllByBoardArticleOrderByIdAsc(boardArticle);
        return uploadedFiles.stream().map(ResourceDTO::new).collect(Collectors.toList());
    }
}