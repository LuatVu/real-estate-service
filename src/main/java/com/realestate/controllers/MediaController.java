package com.realestate.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.realestate.exception.UnsupportedMediaTypeException;
import com.realestate.services.MediaService;
import com.realestate.dto.ApiResponseDto;
import com.realestate.dto.ImagesDto;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<ImagesDto>> uploadDraffImg(@RequestParam("files") String[] files) {
        List<ImagesDto> imagesList = new ArrayList<>();
        List<String> processedFileNames = new ArrayList<>();
        
        for (String file : files) {
            String original = Optional.ofNullable(file)
                    .orElseThrow(() -> new UnsupportedMediaTypeException("Filename is missing"))
                    .toLowerCase();
            
            // Skip duplicate filenames - only allow each file once
            if (processedFileNames.contains(original)) {
                continue; // Skip this duplicate file
            }
            processedFileNames.add(original);
            
            String ext = StringUtils.getFileExtension(original);
            long epochMilli = Instant.now().toEpochMilli();
            String key = String.format("%s-%s.%s", epochMilli, UUID.randomUUID(), ext);
            
            // Create ImagesDto for this file
            ImagesDto imageDto = ImagesDto.builder()
                    .fileName(original)
                    .fileUrl(key) // Using the generated key as fileUrl for draft
                    .build();
            
            imagesList.add(imageDto);
        }
        
        return ResponseEntity.ok(imagesList);
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

    @PostMapping("/delete/image/{imageUrl}")
    public ResponseEntity<ApiResponseDto<?>> postMethodName(@PathVariable String imageUrl) {
        try{
            mediaService.deleteImage(imageUrl);
            return ResponseEntity.ok(ApiResponseDto.builder()
                .status(String.valueOf(HttpStatus.OK))
                .message("Delete image successfully!")
                .build());
        }catch(Exception e){
            ApiResponseDto<?> response = new ApiResponseDto<>("500", e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    
}
