package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.domain.dto.ResourceDTO;
import com.haruhiism.bbs.service.file.FileHandlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@Controller
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final FileHandlerService fileHandlerService;

    @GetMapping("/load")
    @ResponseBody
    public FileSystemResource downloadResource(@RequestParam String hash,
                                               HttpServletResponse response) throws IOException {
        ResourceDTO resourceDTO = fileHandlerService.load(hash);
        Path file = resourceDTO.getFile();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.builder("attachment")
                .filename(resourceDTO.getFilename(), StandardCharsets.UTF_8).build().toString());
        return new FileSystemResource(file);
    }
}
