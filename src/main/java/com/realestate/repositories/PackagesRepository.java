package com.realestate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.realestate.models.Packages;
import java.util.List;

@Repository
public interface PackagesRepository extends JpaRepository<Packages, String> {
    
    @Query("SELECT p FROM Packages p WHERE p.status = 1 ORDER BY p.order ASC")
    List<Packages> findAllActivePackages();
}