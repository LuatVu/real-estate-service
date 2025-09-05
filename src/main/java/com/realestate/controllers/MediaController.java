package com.realestate.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.realestate.exception.UnsupportedMediaTypeException;
import com.realestate.services.MediaService;

import lombok.RequiredArgsConstructor;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {
    private final MediaService mediaService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String key = mediaService.uploadFile(file);
            return ResponseEntity.ok(key);
        } catch (FileUploadException | UnsupportedMediaTypeException e) {
            // Handle exceptions
            return ResponseEntity.badRequest().body("Error uploading file: " + e.getMessage());
        }
    }

    @GetMapping(value = "/image/{key}")
    public ResponseEntity<Resource> getImage(@PathVariable String key) {
        String fullPath = "images/" + key;
        try {
            Resource resource = mediaService.downloadFile(fullPath);
            
            if (!resource.exists() || !resource.isReadable()) {
                System.out.println("Resource exists: " + resource.exists() + ", isReadable: " +
                        (resource.exists() ? resource.isReadable() : "N/A"));
                return ResponseEntity.notFound().build();
            }

            String contentType = mediaService.getContentType(fullPath);

            // Ensure it's an image content type
            if (!contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

}
