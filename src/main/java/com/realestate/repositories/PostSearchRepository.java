package com.realestate.repositories;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.realestate.models.PostsDocument;

public interface PostSearchRepository extends ElasticsearchRepository<PostsDocument, String>{    
    
}
