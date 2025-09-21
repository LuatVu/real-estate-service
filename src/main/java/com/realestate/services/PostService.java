package com.realestate.services;

import java.util.List;

import com.realestate.dto.PostDto;
import com.realestate.models.Posts;

import com.realestate.dto.PostRequest;

public interface PostService {
    Posts createPost(PostDto post) throws Exception;
    PostDto getPost(String postId) throws Exception;
    List<PostDto> getPost(PostRequest postRequest, String userId) throws Exception;
    void updatePostStatus(String postId, String status) throws Exception;

}
