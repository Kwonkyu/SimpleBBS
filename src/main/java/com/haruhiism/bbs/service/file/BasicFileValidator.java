package com.haruhiism.bbs.service.file;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicFileValidator implements FileValidator {

    private String getExtension(String filename){
        if(filename == null || filename.isBlank() || !filename.contains(".")) return "";
        String[] split = filename.split("\\.");
        return split[split.length-1];
    }

    @Override
    public Optional<String> validate(File file) {
        // TODO: implement logic here.
        return Optional.of(String.format("%s.%s", UUID.randomUUID(), getExtension(file.getName())));
    }

    @Override
    public Optional<String> validate(MultipartFile file) {
        return Optional.of(String.format("%s.%s", UUID.randomUUID(), getExtension(file.getOriginalFilename())));
    }

    @Override
    public Optional<String> validate(Path path) {
        throw new RuntimeException("NOT IMPLEMENTED");
    }
}
