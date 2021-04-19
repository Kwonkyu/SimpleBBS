package com.haruhiism.bbs.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public interface FileValidator {

    public Optional<String> validate(File file);

    public Optional<String> validate(MultipartFile file);

    public Optional<String> validate(Path path);


}
