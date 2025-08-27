package com.realestate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.models.Provinces;

public interface ProvinceRepository extends JpaRepository<Provinces, String> {
    
}
