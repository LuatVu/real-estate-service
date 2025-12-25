package com.realestate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.realestate.models.Provinces;

@Repository
public interface ProvinceRepository extends JpaRepository<Provinces, String> {
    
}
