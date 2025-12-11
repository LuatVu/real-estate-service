package com.realestate.services;

import java.util.List;

import com.realestate.dto.PostDto;
import com.realestate.models.Posts;

import com.realestate.dto.PostRequest;
import com.realestate.dto.PostSearchRequest;
import org.springframework.data.domain.Page;

public interface PostService {
    Posts createPost(PostDto post) throws Exception;
    PostDto getPost(String postId) throws Exception;
    List<PostDto> getPost(PostRequest postRequest, String userId) throws Exception;
    void updatePostStatus(String postId, String status) throws Exception;
    Page<PostDto> searchPosts(PostSearchRequest request, int page, int size) throws Exception;
    void reupPost(String postId) throws Exception;
    void renewPost(String postId) throws Exception;
}
