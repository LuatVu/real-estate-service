package com.realestate.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.realestate.dao.UserDetailsImpl;
import com.realestate.dto.ApiResponseDto;
import com.realestate.dto.ImagesDto;
import com.realestate.dto.PostDto;
import com.realestate.dto.PostSearchRequest;
import com.realestate.models.Posts;
import com.realestate.models.PostsDocument;
import com.realestate.services.ElasticSearchService;
import com.realestate.services.FTPService;
import com.realestate.services.PostService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/posts")
public class PostsController {
    @Autowired
    private PostService postService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private FTPService ftpService;

    @Value("${ftp.base-directory}")
    private String baseDirectory;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponseDto<?>> uploadPosts(@RequestPart("files") MultipartFile[] files,
            @RequestPart("data") PostDto data) throws Exception {
        try {
            List<ImagesDto> imageList = new ArrayList<ImagesDto>();
            for (MultipartFile file : files) {
                String fileName = ftpService.uploadFile(file);
                ImagesDto imageDto = ImagesDto.builder().filePath(baseDirectory + fileName).fileName(fileName).build();
                imageList.add(imageDto);
            }

            if (imageList.size() > 0) {
                imageList.get(0).setIsPrimary(true);
            }
            data.setImages(imageList);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            data.setUserId(userPrincipal.getId());

            Posts postJPA = postService.createPost(data);

            return ResponseEntity.ok(ApiResponseDto.builder()
                    .status(String.valueOf(HttpStatus.OK))
                    .message("Upload a post successfully!")
                    .response(postJPA)
                    .build());
        } catch (Exception e) {
            ApiResponseDto<?> response = new ApiResponseDto<>("500", "Internal Server Error: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }    
}