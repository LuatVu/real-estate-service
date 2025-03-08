package com.realestate.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import com.realestate.dto.PostSearchRequest;
import com.realestate.models.Posts;
import com.realestate.models.PostsDocument;
import com.realestate.repositories.PostRepository;
import com.realestate.repositories.PostSearchRepository;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;

@Service
@Slf4j
public class ElasticSearchService {
    @Autowired
    private PostSearchRepository postSearchRepo;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    public Page<PostsDocument> fullTextSearch(PostSearchRequest request, int page, int size){
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.should(QueryBuilders.matchQuery("title", request.getQuery()));
        boolQuery.should(QueryBuilders.matchQuery("description", request.getQuery()));
        boolQuery.should(QueryBuilders.matchQuery("legal", request.getQuery()));
        boolQuery.should(QueryBuilders.matchQuery("province", request.getQuery()));
        boolQuery.should(QueryBuilders.matchQuery("district", request.getQuery()));
        boolQuery.should(QueryBuilders.matchQuery("ward", request.getQuery()));
        boolQuery.should(QueryBuilders.matchQuery("address", request.getQuery()));
        boolQuery.should(QueryBuilders.matchQuery("direction", request.getQuery()));
        boolQuery.should(QueryBuilders.matchQuery("type", request.getQuery()));

        boolQuery.minimumShouldMatch(1);
        TermQueryBuilder statusFilter = QueryBuilders.termQuery("status.keyword", "PUBLISHED");
        boolQuery.filter(statusFilter);

        Pageable pageable = PageRequest.of(
            page, 
            size, 
            Sort.by("_score").descending()
        );

        List<Query> filters = new ArrayList<>();
        filters.add(Query.of(f -> f.term(t -> t.field("status.keyword").value("PUBLISHED"))));

        if (request.getProvinceCode() != null && request.getProvinceCode().trim() != "") {
            filters.add(Query.of(f -> f.term(t -> t.field("provinceCode.keyword").value(request.getProvinceCode()))));
        }

        if (request.getDistrictCode() != null && request.getDistrictCode().trim() != "") {
            filters.add(Query.of(f -> f.term(t -> t.field("districtCode.keyword").value(request.getDistrictCode()))));
        }

        if (request.getWardCode() != null && request.getWardCode().trim() != "") {
            filters.add(Query.of(f -> f.term(t -> t.field("wardCode.keyword").value(request.getWardCode()))));
        }

        if (request.getTypeCode() != null && request.getTypeCode().trim() != "") {
            filters.add(Query.of(f -> f.term(t -> t.field("typeCode.keyword").value(request.getTypeCode()))));
        }

        if (request.getMinPrice() != null) {
            filters.add(Query.of(f -> f.range(RangeQuery.of( r -> r.term(m -> m.field("price").gte(request.getMinPrice().toString() ) ) ) ) ) );
        }

        if(request.getMaxPrice()!= null){
            filters.add(Query.of(f -> f.range(RangeQuery.of( r -> r.term(m -> m.field("price").lte(request.getMaxPrice().toString() ) ) ) ) ) );     
        }

        if(request.getMinAcreage()!= null){
            filters.add(Query.of(f -> f.range(RangeQuery.of( r -> r.term(m -> m.field("acreage").gte(request.getMinAcreage().toString() ) ) ) ) ) );
        }

        if(request.getMaxAcreage() != null){
            filters.add(Query.of(f -> f.range(RangeQuery.of( r -> r.term(m -> m.field("acreage").lte(request.getMaxAcreage().toString() ) ) ) ) ) );
        }

        NativeQuery searchQuery =  NativeQuery.builder()
                .withQuery(q -> q
                .bool(b -> b
                    .should(s -> s.match(m -> m.field("title").query(request.getQuery())))
                    .should(s -> s.match(m -> m.field("description").query(request.getQuery())))
                    .should(s -> s.match(m -> m.field("legal").query(request.getQuery())))
                    .should(s -> s.match(m -> m.field("province").query(request.getQuery())))
                    .should(s -> s.match(m -> m.field("district").query(request.getQuery())))
                    .should(s -> s.match(m -> m.field("ward").query(request.getQuery())))
                    .should(s -> s.match(m -> m.field("address").query(request.getQuery())))
                    .should(s -> s.match(m -> m.field("direction").query(request.getQuery())))
                    .should(s -> s.match(m -> m.field("type").query(request.getQuery())))
                    .minimumShouldMatch("1")
                    .filter(filters)
                ))
                
                .withPageable(pageable) // Add pagination
                .build();

        SearchHits<PostsDocument> searchHits = elasticsearchOperations.search(searchQuery, PostsDocument.class);

        List<PostsDocument> results = searchHits.stream()
        .map(hit -> hit.getContent())
        .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, searchHits.getTotalHits());
    }

    @Transactional
    public void syncToElasticSearch(){
        List<Posts> posts = postRepository.findAll();
        List<PostsDocument> documents = posts.stream().map(PostsDocument::fromEntity).collect(Collectors.toList());
        postSearchRepo.saveAll(documents);
    }
}
