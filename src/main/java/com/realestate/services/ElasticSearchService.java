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
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import com.realestate.dto.PostSearchRequest;
import com.realestate.models.Posts;
import com.realestate.models.PostsDocument;
import com.realestate.repositories.PostRepository;
import com.realestate.repositories.PostSearchRepository;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import org.springframework.data.elasticsearch.core.SearchHits;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

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
        
        Pageable pageable = PageRequest.of(
            page, 
            size, 
            Sort.by(Sort.Order.desc("priorityLevelValue"), Sort.Order.desc("bumpTime"))
        );

        List<Query> filters = new ArrayList<>();
        filters.add(Query.of(f -> f.term(t -> t.field("status.keyword").value("PUBLISHED"))));

        if (request.getCityCode() != null && request.getCityCode().trim() != "") {
            filters.add(Query.of(f -> f.term(t -> t.field("provinceCode.keyword").value(request.getCityCode()))));
        }

        if (request.getDistrictCode() != null && request.getDistrictCode().trim() != "") {
            filters.add(Query.of(f -> f.term(t -> t.field("districtCode.keyword").value(request.getDistrictCode()))));
        }       

        if(request.getWardCodes() != null && request.getWardCodes().size() > 0){
            filters.add(Query.of(q -> q.bool(
                b -> {
                    BoolQuery.Builder builder = b;
                    request.getWardCodes().forEach(code -> {
                        builder.should(s -> s.term(t -> t.field("wardCode.keyword").value(code)));
                    });
                    return builder.minimumShouldMatch("1");
                }
            )));
        }        

        if(request.getTypeCodes() != null && request.getTypeCodes().size() > 0){
            filters.add(Query.of(q -> q.bool(
                b -> {
                    BoolQuery.Builder builder = b;
                    request.getTypeCodes().forEach(code -> {
                        builder.should(s -> s.term(t -> t.field("typeCode.keyword").value(code)));
                    });
                    return builder.minimumShouldMatch("1");
                }
            )));
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

        NativeQuery searchQuery ;

        if(request.getQuery() != null && request.getQuery().trim() != ""){
            searchQuery =  NativeQuery.builder()
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
                    .should(s -> s.match(m -> m.field("keywords").query(request.getQuery())))
                    .minimumShouldMatch("1")
                    .filter(filters)
                ))
                
                .withPageable(pageable) // Add pagination
                .build();
        }else{
            searchQuery =  NativeQuery.builder()
                            .withQuery(q -> q.queryString(qs -> qs.query("*")))
                            .withQuery(q -> q.bool(b -> b.filter(filters)))
                            .withPageable(pageable)                            
                            .build();            
        }

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
