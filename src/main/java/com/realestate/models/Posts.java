package com.realestate.models;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Data
@Entity
@Table(name = "posts")
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "acreage", precision = 15, scale = 2)
    private BigDecimal acreage;

    @Column(name = "bedrooms")
    private Integer bedrooms;

    @Column(name = "bathrooms")
    private Integer bathrooms;

    @Enumerated(EnumType.STRING)
    @Column(name = "furniture")
    private FurnitureType furniture;

    @Enumerated(EnumType.STRING)
    @Column(name = "legal")
    private LegalType legal;

    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "address", columnDefinition = "text")
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PostStatus status = PostStatus.DRAFT;

    @Column(name = "floors")
    private Integer floors;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction")
    private Direction direction;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Images> images;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

    public enum FurnitureType {
        DAY_DU("DAY DU"),
        CO_BAN("CO BAN"),
        KHONG_NOI_THAT("KHONG NOI THAT");

        private final String value;

        FurnitureType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum LegalType {
        SO_DO("SO DO"),
        HOP_DONG_MUA_BAN("HOP DONG MUA BAN"),
        KHONG_SO("KHONG SO");

        private final String value;

        LegalType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum PostStatus {
        DRAFT,
        PUBLISHED,
        EXPIRED,
        DELETED
    }

    public enum Direction {
        DONG,
        TAY,
        NAM,
        BAC,
        DONG_BAC,
        TAY_BAC,
        DONG_NAM,
        TAY_NAM
    }
}
