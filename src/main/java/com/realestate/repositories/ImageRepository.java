package com.realestate.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.realestate.models.Images;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Images, String>{
    @Query("SELECT i FROM Images i WHERE i.post.postId = :postId AND i.isPrimary = true AND i.status = 1")
    Optional<Images> findPrimaryImage(String postId);

    @Query("UPDATE Images i SET i.status = :status WHERE i.imageId = :imageId")
    void updateImageStatus(String imageId, boolean status);
}
