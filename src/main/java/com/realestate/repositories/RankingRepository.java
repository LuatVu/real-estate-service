package com.realestate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import com.realestate.models.Ranking;

public interface RankingRepository extends JpaRepository<Ranking, String>{
    @Query("SELECT r FROM Ranking r WHERE r.post.postId = :postId")
    Optional<Ranking> findByPostId(@Param("postId") String postId);
}
