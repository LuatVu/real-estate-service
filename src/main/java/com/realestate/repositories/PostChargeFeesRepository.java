package com.realestate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import com.realestate.models.PostChargeFees;
import com.realestate.models.Ranking.PriorityLevel;
import java.util.Optional;

@Repository
public interface PostChargeFeesRepository extends JpaRepository<PostChargeFees, String>{
    @Query("SELECT rf FROM PostChargeFees rf WHERE rf.priorityLevel = :priorityLevel")
    Optional<PostChargeFees> findByPriorityLevel(@Param("priorityLevel") PriorityLevel priorityLevel);
}
