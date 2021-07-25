package com.haruhiism.bbs.service.file;

import com.haruhiism.bbs.domain.dto.ResourceDTO;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.UploadedFile;
import com.haruhiism.bbs.exception.article.NoArticleFoundException;
import com.haruhiism.bbs.exception.resource.InvalidFileException;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.repository.ResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
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
@Profile("!publish")
public class LocalFileHandlerService implements FileHandlerService {

    private final Path uploadedFilePath;
    private final FileValidator fileValidator;
    private final ResourceRepository resourceRepository;
    private final ArticleRepository articleRepository;

    public LocalFileHandlerService(FileValidator fileValidator,
                                   ResourceRepository resourceRepository,
                                   ArticleRepository articleRepository) throws IOException {
        uploadedFilePath = Files.createTempDirectory("SimpleBBS");
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

            log.info("successfully transferred file {} as {}", uploadedFile.getOriginalFilename(), validFile.toAbsolutePath());
            resourceRepository.save(new UploadedFile(Objects.requireNonNull(uploadedFile.getOriginalFilename()), hashedValidFilename, article));
        } catch (IOException e) {
            log.error("uploading file {} failed({}).", uploadedFile.getOriginalFilename(), e.getLocalizedMessage());
        }
    }

    @Override
    public void store(List<MultipartFile> files, Long articleId) {
        BoardArticle article = articleRepository.findById(articleId).orElseThrow(NoArticleFoundException::new);
        for (MultipartFile file : files) {
            if(!file.isEmpty()) store(file, article);
        }
    }

    private Path getFromUploadFilePath(String filename){
        Path actualFile = uploadedFilePath.resolve(filename);
        if(!Files.exists(actualFile)){
            throw new InvalidFileException();
        }

        return actualFile;
    }

    @Override
    public void delete(List<String> deletedHashedFilenames, Long articleId) {
        BoardArticle article = articleRepository.findById(articleId).orElseThrow(NoArticleFoundException::new);
        List<String> hashedFilenames = resourceRepository.findAllByBoardArticleOrderByIdAsc(article)
                .stream().map(UploadedFile::getHashedFilename).collect(Collectors.toList());

        for (String deletedHashedFilename : deletedHashedFilenames) {
            if (!hashedFilenames.contains(deletedHashedFilename)) {
                throw new InvalidFileException();
            }

            resourceRepository.deleteByHashedFilename(deletedHashedFilename);
            Path deletedFile = getFromUploadFilePath(deletedHashedFilename);
//            deletedFile.toFile().delete(); use Files's delete method!
            try {
                Files.delete(deletedFile);
            } catch (IOException e){
                log.error("file {} cannot be deleted.", deletedFile.toAbsolutePath().toFile());
            }
        }
    }

    @Override
    public ResourceDTO load(String hashedFilename) {
        Path actualFile = getFromUploadFilePath(hashedFilename);

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