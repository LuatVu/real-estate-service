package com.realestate.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.realestate.dto.ApiResponseDto;
import com.realestate.dto.ImagesDto;
import com.realestate.dto.PostDto;
import com.realestate.models.Posts;
import com.realestate.services.FTPService;
import com.realestate.services.PostService;

import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequestMapping("/api/posts")
public class PostsController {
    @Autowired
    private PostService postService;

    @Autowired
    private FTPService ftpService;

    @Value("${ftp.base-directory}")
    private String baseDirectory;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponseDto<?>> uploadPosts(@RequestPart("files") MultipartFile[] files, @RequestPart("data") PostDto data) throws Exception {        
        try{
            List<ImagesDto> imageList = new ArrayList<ImagesDto>();
            for (MultipartFile file : files) {
                String fileName = ftpService.uploadFile(file);
                ImagesDto imageDto = ImagesDto.builder().filePath(baseDirectory + fileName).fileName(fileName).build();
                imageList.add(imageDto);
            }
    
            if(imageList.size() > 0){
                imageList.get(0).setIsPrimary(true);
            }
            data.setImages(imageList);
            // for test
            data.setUserId("cbe24f08-1a66-4fce-9d33-9379316e0e4e");

            Posts postJPA = postService.createPost(data);
    
            return ResponseEntity.ok(ApiResponseDto.builder()
            .status(String.valueOf(HttpStatus.OK))
            .message("Upload a post successfully!")
            .response(postJPA)
            .build());
        }catch(Exception e){
            ApiResponseDto<?> response = new ApiResponseDto<>("500", "Internal Server Error: " + e.getMessage(), null);
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
                
    }
    
}
