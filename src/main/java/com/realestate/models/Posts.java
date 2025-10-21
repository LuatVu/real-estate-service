package com.realestate.models;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.facebook.ads.sdk.Transaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Data
@Entity
@Table(name = "posts")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Posts implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "post_id")    
    private String postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
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

    @Column(name = "province_code", length = 20)
    private String provinceCode;

    @Column(name = "district_code", length = 20)
    private String districtCode;

    @Column(name = "ward_code", length = 20)
    private String wardCode;

    @Column(name = "address", columnDefinition = "text")
    private String address;

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

    @Enumerated(EnumType.STRING)
    @Column(name="type")
    private Type type;

    @Column(name = "frontage")
    private Integer frontage;
    
    @Enumerated(EnumType.STRING)
    @Column(name= "transaction_type", length = 20)
    private TransactionType transactionType;


    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Images> images;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Ranking ranking;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

    public enum Type {
        CHCC, NHA_RIENG, BIET_THU,
        NHA_PHO, DAT_NEN, CONDOTEL, KHO_NHA_XUONG,
        BDS_KHAC
    }

    public enum FurnitureType {
        DAY_DU("DAY_DU"),
        CO_BAN("CO_BAN"),
        KHONG_NOI_THAT("KHONG_NOI_THAT");

        private final String value;

        FurnitureType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }        
    }

    public enum TransactionType{
        SELL, RENT, PROJECT
    }

    public enum LegalType {
        SO_DO("SO_DO"),
        HOP_DONG_MUA_BAN("HOP_DONG_MUA_BAN"),
        KHONG_SO("KHONG_SO");

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

    public enum PriorityLevel {
        DIAMOND(4),   // Highest priority
        GOLD(3),
        SILVER(2),
        NORMAL(1);    // Lowest priority
    
        private final int priority;
    
        PriorityLevel(int priority) {
            this.priority = priority;
        }
    
        public int getPriority() {
            return priority;
        }
    }
}
