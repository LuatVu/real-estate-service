package com.realestate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.models.Posts;

public interface PostRepository extends JpaRepository<Posts, String>{
    
}
