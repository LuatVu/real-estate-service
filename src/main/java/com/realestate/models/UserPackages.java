package com.realestate.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_packages")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class UserPackages implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_package_id")    
    private String userPackageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private Packages packages;

    @Column(name = "remaining_diamond_posts")
    private Integer remainingDiamondPosts;

    @Column(name = "remaining_gold_posts")
    private Integer remainingGoldPosts;

    @Column(name = "remaining_silver_posts")
    private Integer remainingSilverPosts;

    @Column(name = "remaining_normal_posts")
    private Integer remainingNormalPosts;

    @Column(name = "active_date")
    private LocalDateTime activeDate;

    @Column(name = "expired_date")
    private LocalDateTime expiredDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        ACTIVE,
        INACTIVE,
        EXPIRED
    }
}
