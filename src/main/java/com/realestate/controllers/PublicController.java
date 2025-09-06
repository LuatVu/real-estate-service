package com.realestate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.dto.ApiResponseDto;
import com.realestate.dto.PostDto;
import com.realestate.dto.PostSearchRequest;
import com.realestate.models.PostsDocument;
import com.realestate.services.ElasticSearchService;
import com.realestate.services.MediaService;
import com.realestate.services.PostService;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    @Autowired
    private PostService postService;
    @Autowired
    private ElasticSearchService elasticSearchService;
    @Autowired
    private MediaService mediaService;


    @GetMapping("/get-post")
    public ResponseEntity<ApiResponseDto<?>> getPost(@RequestParam("postId") String postId) throws Exception {
        PostDto postDto = postService.getPost(postId);
        if (postDto != null) {
            return ResponseEntity.ok(ApiResponseDto.builder()
                    .status(String.valueOf(HttpStatus.OK))
                    .response(postDto)
                    .build());
        }else{
            ApiResponseDto<?> response = new ApiResponseDto<>("404", "Not Found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

    }

    @PostMapping("/search-post")
    public ResponseEntity<Page<PostsDocument>> searchPosts(
            @RequestBody PostSearchRequest searchRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostsDocument> results = elasticSearchService.fullTextSearch(searchRequest, page, size);
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = "/image/{fileName}")
    public ResponseEntity<Resource>  download(@PathVariable String fileName) {
        String fullPath = "images/" + fileName;
        try{
            Resource resource = mediaService.downloadFile(fullPath);
            if(resource == null || !resource.exists() || !resource.isReadable()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            String contentType = mediaService.getContentType(fullPath);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
                    .body(resource);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
