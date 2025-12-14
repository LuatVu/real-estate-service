package com.realestate.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.dao.UserDetailsImpl;
import com.realestate.dto.ApiResponseDto;
import com.realestate.dto.PostChargeFeeDto;
import com.realestate.dto.PostDto;
import com.realestate.dto.PostRequest;
import com.realestate.models.Posts.PostStatus;
import com.realestate.services.PostService;
import com.realestate.utilities.EnumUtils;

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

    @PostMapping("/update-post-status")
    public ResponseEntity<ApiResponseDto<?>> updatePostStatus(@RequestParam String postId,
            @RequestParam String status) {
        
        if (postId == null || postId.isEmpty() || status == null || status.isEmpty()) {
            ApiResponseDto<?> response = new ApiResponseDto<>("400", "Bad Request: postId and status are required",
                    null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        try {
            if (EnumUtils.fromString(PostStatus.class, status) == null) {
                ApiResponseDto<?> response = new ApiResponseDto<>("400", "Bad Request: Invalid status value", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (IllegalArgumentException e) {
            ApiResponseDto<?> response = new ApiResponseDto<>("400", "Bad Request: Invalid status value", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            postService.updatePostStatus(postId, status);
            return ResponseEntity.ok(ApiResponseDto.builder()
                    .status(String.valueOf(HttpStatus.OK))
                    .message("Update a post successfully!")
                    .response(postId)
                    .build());
        } catch (Exception e) {
            ApiResponseDto<?> response = new ApiResponseDto<>("500", "Internal Server Error: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/posts/reup/{postId}")
    public ResponseEntity<ApiResponseDto<?>> reupPost(@PathVariable String postId) {
        try{
            postService.reupPost(postId);
            return ResponseEntity.ok(ApiResponseDto.builder()
                    .status(String.valueOf(HttpStatus.OK))
                    .message("Reup post successfully!")
                    .build());
        }catch(Exception e){
            ApiResponseDto<?> response = new ApiResponseDto<>("500", "Internal Server Error: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/posts/renew/{postId}")
    public ResponseEntity<ApiResponseDto<?>> renewPost(@PathVariable String postId) {
        try{
            postService.renewPost(postId);
            return ResponseEntity.ok(ApiResponseDto.builder()
                    .status(String.valueOf(HttpStatus.OK))
                    .message("Renew post successfully!")
                    .build());
        }catch(Exception e){
            ApiResponseDto<?> response = new ApiResponseDto<>("500", "Internal Server Error: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/posts/charge-fee/{postId}")
    public ResponseEntity<ApiResponseDto<?>> getMethodName(@PathVariable String postId) {
        try{
            PostChargeFeeDto chargeFeeDto = postService.getPostChargeFee(postId);
            return ResponseEntity.ok(ApiResponseDto.builder()
                    .status(String.valueOf(HttpStatus.OK))
                    .response(chargeFeeDto)
                    .build());
        }catch(Exception e){
            ApiResponseDto<?> response = new ApiResponseDto<>("500", "Internal Server Error: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
