package com.haruhiism.bbs.controller;

import com.haruhiism.bbs.domain.dto.ResourceDTO;
import com.haruhiism.bbs.service.file.FileHandlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final FileHandlerService fileHandlerService;

    @GetMapping("/load")
    @ResponseBody
    public void downloadResource(@RequestParam String hash,
                                 HttpServletResponse response) throws IOException {
        ResourceDTO resourceDTO = fileHandlerService.load(hash);
        Path file = resourceDTO.getFile();
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Disposition
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setContentLength((int)Files.size(file));
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", resourceDTO.getFilename()));
        Files.copy(file, response.getOutputStream());
        response.flushBuffer();
    }
}
