package com.haruhiism.bbs.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public interface FileValidator {

    /**
     * Validate file.
     * @param file File object containing file.
     * @return Optional\<String> object containing validated file's name.
     */
    Optional<String> validate(File file);

    /**
     * Validate file.
     * @param file MultipartFile object containing file.
     * @return Optional\<String> object containing validated file's name.
     */
    Optional<String> validate(MultipartFile file);

    /**
     * Validate file.
     * @param path Path object containing file.
     * @return Optional\<String> object containing validated file's name.
     */
    Optional<String> validate(Path path);
}
