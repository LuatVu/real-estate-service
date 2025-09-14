package com.realestate.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.realestate.models.Posts;
import com.realestate.models.Posts.TransactionType;

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
}
