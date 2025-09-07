package com.realestate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.realestate.dao.UserDetailsImpl;
import com.realestate.dto.ApiResponseDto;
import com.realestate.dto.PostDto;
import com.realestate.models.Posts;
import com.realestate.services.PostService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/posts")
public class PostsController {
    @Autowired
    private PostService postService;    

    @PostMapping("/upload")
    public ResponseEntity<ApiResponseDto<?>> uploadPosts(@RequestBody PostDto data) throws Exception {
        try {
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