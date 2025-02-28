package com.realestate.repositories;



import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.models.Images;

public interface ImageRepository extends JpaRepository<Images, String>{

}
