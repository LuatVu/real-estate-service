package com.realestate.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.realestate.exception.UnsupportedMediaTypeException;
import com.realestate.services.MediaService;

import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.realestate.utilities.StringUtils;
import java.time.Instant;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {
    private final MediaService mediaService;

    @PostMapping("/draft/upload")
    public ResponseEntity<String> postMethodName(@RequestParam("file") MultipartFile file) {
        // TODO: process POST request
        String original = Optional.ofNullable(file.getOriginalFilename())
                .orElseThrow(() -> new UnsupportedMediaTypeException("Filename is missing"))
                .toLowerCase();
        Optional.ofNullable(file.getContentType())
                .orElseThrow(() -> new UnsupportedMediaTypeException("Content-Type is unknown"));
        
        String ext = StringUtils.getFileExtension(original);        
        long epochMilli = Instant.now().toEpochMilli();
        String key = String.format("%s-%s.%s", epochMilli, UUID.randomUUID(), ext);

        return ResponseEntity.ok(key);
    }

    @PostMapping("/upload/{key}")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String key) {
        try {
            String original = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new UnsupportedMediaTypeException("Filename is missing"))
                    .toLowerCase();
            String contentType = Optional.ofNullable(file.getContentType())
                    .orElseThrow(() -> new UnsupportedMediaTypeException("Content-Type is unknown"));
            String ext = StringUtils.getFileExtension(original);
            String folder = switch (ext) {
                case "jpg", "jpeg", "png", "gif", "webp", "bmp", "svg" -> "images";
                // case "mp4","mov" -> "videos";
                // case "pdf","doc","docx","txt" -> "documents";
                default -> throw new UnsupportedMediaTypeException("Unsupported file type: " + contentType);
            };
            mediaService.uploadFile(file, folder + "/" + key);
            return ResponseEntity.ok(key);
        } catch (FileUploadException | UnsupportedMediaTypeException e) {
            // Handle exceptions
            return ResponseEntity.badRequest().body("Error uploading file: " + e.getMessage());
        }
    }
    
}
