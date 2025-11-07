package com.realestate.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "packages")
public class Packages implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "package_id")    
    private String packageId;

    @Column(name = "package_name", nullable = false, length = 100)
    private String packageName;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "max_diamond_posts", nullable = false)
    private Integer maxDiamondPosts;

    @Column(name = "max_gold_posts", nullable = false)
    private Integer maxGoldPosts;

    @Column(name = "max_silver_posts", nullable = false)
    private Integer maxSilverPosts;

    @Column(name = "max_normal_posts", nullable = false)
    private Integer maxNormalPosts;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "status", columnDefinition = "int default 1")
    private Integer status;

    @Column(name = "order", nullable = true)
    private Integer order;

    @Column(name="image", nullable = true)
    private String image;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
