package com.haruhiism.bbs.service.file;

import com.haruhiism.bbs.domain.dto.ResourceDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileHandlerService {
    /**
     * Store files to storage.
     * @param files files to be uploaded
     * @param articleId owner article of files
     */
    public void store(List<MultipartFile> files, Long articleId);

    /**
     * Load specific file.
     * @param hashedFilename File's hash value.
     * @return ResourceDTO object filled with file information.
     */
    public ResourceDTO load(String hashedFilename);

    /**
     * Delete files from article.
     * @param deletedHashedFilenames Hash values of deleting files.
     * @param articleId Article's id.
     */
    public void delete(List<String> deletedHashedFilenames, Long articleId);

    /**
     * Get files of article.
     * @param articleId Article's id.
     * @return List of ResourceDTO objects containing files information.
     */
    public List<ResourceDTO> listResourcesOfArticle(Long articleId);
}
