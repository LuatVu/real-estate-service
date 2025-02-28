package com.realestate.services;

import com.realestate.dto.PostDto;
import com.realestate.models.Posts;

public interface PostService {
    Posts createPost(PostDto post) throws Exception;
}
