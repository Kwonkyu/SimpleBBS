package com.haruhiism.bbs.service.file;

import com.haruhiism.bbs.domain.dto.ResourceDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileHandlerService {
    public void store(List<MultipartFile> files, Long articleId);

    public ResourceDTO load(String hashedFilename);

    public List<ResourceDTO> listResourcesOfArticle(Long articleId);
}
