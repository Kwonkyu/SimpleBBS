package com.haruhiism.bbs.service.file;

import com.cloudinary.Cloudinary;
import com.cloudinary.Singleton;
import com.cloudinary.utils.ObjectUtils;
import com.haruhiism.bbs.domain.dto.ResourceDTO;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.UploadedFile;
import com.haruhiism.bbs.exception.article.NoArticleFoundException;
import com.haruhiism.bbs.exception.resource.InvalidFileException;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.repository.ResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@Profile("publish")
public class CloudinaryFileHandlerService implements FileHandlerService{
;
    private final Path temporaryFilePath;
    private final FileValidator fileValidator;
    private final ResourceRepository resourceRepository;
    private final ArticleRepository articleRepository;

    /**
     * CLOUDINARY MEMOS
     * - Parameter names: snake(i.e. public_id)
     * - Class names: Pascal(i.e. ClassLoader)
     * - Method names: camels(i.e. getInstance)
     * - Parameter type: Map
     */
    private final Cloudinary cloudinary;

    public CloudinaryFileHandlerService(FileValidator fileValidator,
                                        ResourceRepository resourceRepository,
                                        ArticleRepository articleRepository) throws IOException {
        this.fileValidator = fileValidator;
        this.resourceRepository = resourceRepository;
        this.articleRepository = articleRepository;

        temporaryFilePath = Files.createTempDirectory("SimpleBBS");
        cloudinary = Singleton.getCloudinary(); // available if CLOUDINARY_URL env var is set.
    }

    private void store(MultipartFile file, BoardArticle article) {
        try {
            fileValidator.validate(file).orElseThrow(InvalidFileException::new);
            log.info("validated file {}", file.getOriginalFilename());

            Path validFile = Paths.get(temporaryFilePath.toString(), file.getOriginalFilename());
            file.transferTo(validFile);

            // directly upload to cloudinary doesn't have filename.
            Map result = cloudinary.uploader().upload(
                    validFile.toAbsolutePath().toString(), ObjectUtils.asMap(
                    "folder", String.format("%s/", article.getId()),
                    "resource_type", "raw", // set resource type raw to bypass pdf filtering.
                    "use_filename", true));
            Files.delete(validFile);

            String cloudinaryPublicId = (String) result.get("public_id");
            log.info("uploaded file to cloudinary(public_id: {})", cloudinaryPublicId);
            UploadedFile uploadedFile = new UploadedFile(Objects.requireNonNull(file.getOriginalFilename()), cloudinaryPublicId, article);
            uploadedFile.registerRemoteUrl((String) result.get("url"));

            resourceRepository.save(uploadedFile);
        } catch (IOException e){
            log.error("uploading file {} failed({}).", file.getOriginalFilename(), e.getLocalizedMessage());
        }
    }

    @Override
    public void store(List<MultipartFile> files, Long articleId){
        log.debug("uploading files to article #{}", articleId);
        BoardArticle article = articleRepository.findById(articleId).orElseThrow(NoArticleFoundException::new);
        for (MultipartFile file : files) {
            if(!file.isEmpty()) store(file, article);
        }
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
            try {
                cloudinary.uploader().rename(
                        deletedHashedFilename,
                        String.format("deleted/%s", deletedHashedFilename),
                        ObjectUtils.asMap(
                                "invalidate", true
                        ));
                log.info("removed file {}", deletedHashedFilename);
            } catch (IOException e){
                log.error("deleting file failed({})", e.getLocalizedMessage());
            }
        }
    }

    @Override
    public ResourceDTO load(String hashedFilename) {
        throw new NotImplementedException();
    }

    @Override
    public List<ResourceDTO> listResourcesOfArticle(Long articleId) {
        BoardArticle article = articleRepository.findById(articleId).orElseThrow(NoArticleFoundException::new);
        List<UploadedFile> resources = resourceRepository.findAllByBoardArticleOrderByIdAsc(article);
        return resources.stream().map(ResourceDTO::new).collect(Collectors.toList());
    }
}
