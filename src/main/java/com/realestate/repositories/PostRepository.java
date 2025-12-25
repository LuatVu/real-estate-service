package com.realestate.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.realestate.models.Posts;
import com.realestate.models.Posts.TransactionType;

@Repository
public interface PostRepository extends JpaRepository<Posts, String>{
    
    @Query("SELECT p FROM Posts p WHERE " +
           "(:title IS NULL OR :title = '' OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:transactionType IS NULL OR :transactionType = '' OR p.transactionType = :transactionType) AND " +
           "(:dateFilter IS NULL OR p.createdDate >= :dateFilter) AND " +
           "(p.status != 'DELETED') AND " +
           "(:userId IS NULL OR :userId = '' OR p.user.userId = :userId) " +
           "ORDER BY p.createdDate DESC")
    List<Posts> findPostsWithFilters(@Param("title") String title, 
                                    @Param("transactionType") TransactionType transactionType,
                                    @Param("dateFilter") LocalDateTime dateFilter,
                                    @Param("userId") String userId);

    @Query("SELECT p FROM Posts p LEFT JOIN p.ranking r WHERE " +
           "(:query IS NULL OR :query = '' OR LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:minAcreage IS NULL OR p.acreage >= :minAcreage) AND " +
           "(:maxAcreage IS NULL OR p.acreage <= :maxAcreage) AND " +
           "(:isTypeCodesEmpty = true OR CAST(p.type AS string) IN :typeCodes) AND " +
           "(:cityCode IS NULL OR :cityCode = '' OR p.provinceCode = :cityCode) AND " +           
           "(:isWardCodesEmpty = true OR p.wardCode IN :wardCodes) AND " +
           "(:transactionType IS NULL OR p.transactionType = :transactionType) AND " +
           "(p.status = 'PUBLISHED') " +
           "ORDER BY " +
           "CASE r.priorityLevel " +
           "WHEN 'DIAMOND' THEN 4 " +
           "WHEN 'GOLD' THEN 3 " +
           "WHEN 'SILVER' THEN 2 " +
           "WHEN 'NORMAL' THEN 1 " +
           "ELSE 0 " +
           "END DESC, r.bumpTime DESC")
    Page<Posts> searchPosts(@Param("query") String query, 
                           @Param("minPrice") Double minPrice,
                           @Param("maxPrice") Double maxPrice,
                           @Param("minAcreage") Double minAcreage,
                           @Param("maxAcreage") Double maxAcreage,
                           @Param("typeCodes") List<String> typeCodes,
                           @Param("isTypeCodesEmpty") boolean isTypeCodesEmpty,
                           @Param("cityCode") String cityCode,                           
                           @Param("wardCodes") List<String> wardCodes,
                           @Param("isWardCodesEmpty") boolean isWardCodesEmpty,
                           @Param("transactionType") TransactionType transactionType,
                           Pageable pageable);

}
