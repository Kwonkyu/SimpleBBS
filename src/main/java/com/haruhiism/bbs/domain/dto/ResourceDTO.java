package com.haruhiism.bbs.domain.dto;

import com.haruhiism.bbs.domain.entity.UploadedFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ResourceDTO {
    private String filename;
    private String hashedFilename;
    private String remoteUrl;
    private Path file;

    public ResourceDTO(UploadedFile uploadedFile) {
        this.filename = uploadedFile.getFilename();
        this.hashedFilename = uploadedFile.getHashedFilename();
        this.remoteUrl = uploadedFile.getRemoteUrl();
    }
}
