package com.haruhiism.bbs.service.file;

import com.cloudinary.Cloudinary;
import com.cloudinary.Singleton;
import com.cloudinary.utils.ObjectUtils;
import com.haruhiism.bbs.domain.dto.ResourceDTO;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.domain.entity.UploadedFile;
import com.haruhiism.bbs.exception.InvalidFileException;
import com.haruhiism.bbs.exception.NoArticleFoundException;
import com.haruhiism.bbs.repository.ArticleRepository;
import com.haruhiism.bbs.repository.ResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@Primary
public class CloudinaryFileHandlerService implements  FileHandlerService{

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
                                        ArticleRepository articleRepository){
        this.fileValidator = fileValidator;
        this.resourceRepository = resourceRepository;
        this.articleRepository = articleRepository;

        // available if CLOUDINARY_URL env var is set.
        cloudinary = Singleton.getCloudinary();
    }

    @PostConstruct
    public void init(){

    }

    private void store(MultipartFile file, BoardArticle article) {
        // TODO: exception handler for file is null, empty, exceeded size.
        try {
            fileValidator.validate(file).orElseThrow(InvalidFileException::new);
            log.info("validated file {}", file.getOriginalFilename());

            Map result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", String.format("%s/", article.getId()),
                    "resource_type", "auto"));
            log.info("uploaded file to cloudinary");

            String cloudinaryPublicId = (String) result.get("public_id");
            log.info("uploaded file has {} public id", cloudinaryPublicId);
            UploadedFile uploadedFile = new UploadedFile(Objects.requireNonNull(file.getOriginalFilename()), cloudinaryPublicId, article);
            uploadedFile.registerRemoteUrl((String) result.get("url")); // TODO: secure_url for https

            resourceRepository.save(uploadedFile);
            // TODO: apply asynchronous API. when uploading is finished, add database record.
        } catch (IOException e){
            log.error("uploading file {} failed({}).", file.getOriginalFilename(), e.getLocalizedMessage());
        }
    }

    @Override
    public void store(List<MultipartFile> files, Long articleId){
        log.debug("uploading files to article #{}", articleId);
        BoardArticle article = articleRepository.findById(articleId).orElseThrow(NoArticleFoundException::new);
        for (MultipartFile file : files) {
            store(file, article);
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
