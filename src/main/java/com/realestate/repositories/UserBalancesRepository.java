package com.realestate.repositories;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.realestate.models.UserBalances;
import org.springframework.data.repository.query.Param;

@Repository
public interface UserBalancesRepository extends JpaRepository<UserBalances, String> {
    @Query("Select ub from UserBalances ub where ub.user.userId = :userId AND ub.status = 'ACTIVE' AND ub.expiredDate > CURRENT_TIMESTAMP")
    List<UserBalances> findByUserId(@Param("userId") String userId);
}
