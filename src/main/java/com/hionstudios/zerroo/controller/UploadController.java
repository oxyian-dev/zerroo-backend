package com.hionstudios.zerroo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.security.PermitAll;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/uploads")
public class UploadController {
    @GetMapping("{folder}/{filename:.+}")
    @PermitAll
    public ResponseEntity<Resource> serveUpload(@PathVariable String folder, @PathVariable String filename) throws IOException {
        Path base = Paths.get("uploads").toAbsolutePath().normalize();
        Path file = base.resolve(folder).resolve(filename).normalize();
        if (!file.startsWith(base)) {
            return ResponseEntity.badRequest().build();
        }
        Resource resource = new UrlResource(file.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }
        String contentType = Files.probeContentType(file);
        MediaType mediaType = contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
}
