package com.realestate.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.realestate.models.UserPackages;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface UserPackagesRepository extends JpaRepository<UserPackages, String>{
    @Query("Select up from UserPackages up where up.user.userId = :userId AND up.status = 'ACTIVE' AND up.expiredDate > CURRENT_TIMESTAMP AND up.remainingDiamondPosts + up.remainingGoldPosts + up.remainingSilverPosts + up.remainingNormalPosts > 0")
    List<UserPackages> findByUserId(@Param("userId") String userId);
}
