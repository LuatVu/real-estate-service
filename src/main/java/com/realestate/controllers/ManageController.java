package com.realestate.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.dao.UserDetailsImpl;
import com.realestate.dto.ApiResponseDto;
import com.realestate.dto.PostDto;
import com.realestate.dto.PostRequest;
import com.realestate.services.PostService;

@RestController
@RequestMapping("/api/manage")
public class ManageController {
    @Autowired
    private PostService postService;

    @PostMapping("/get-posts")
    public ResponseEntity<ApiResponseDto<?>> getPosts(@RequestBody PostRequest postRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            String userId = userPrincipal.getId();

            List<PostDto> posts = postService.getPost(postRequest, userId);
            return ResponseEntity.ok(ApiResponseDto.builder()
                    .status(String.valueOf(HttpStatus.OK))
                    .message("Get posts successfully!")
                    .response(posts)
                    .build());
        } catch (Exception e) {
            ApiResponseDto<?> response = new ApiResponseDto<>("500", "Internal Server Error: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
