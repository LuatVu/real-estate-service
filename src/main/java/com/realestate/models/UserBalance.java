package com.realestate.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_balances")
public class UserBalance implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "balance_id")    
    private String balanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "user_balances_ibfk_1"))
    @JsonIgnore
    private User user; // Quan hệ nhiều-một với bảng users

    @Column(name = "main_balance", precision = 15, scale = 2, columnDefinition = "DECIMAL(15,2) DEFAULT 0.00")
    private BigDecimal mainBalance = BigDecimal.ZERO;

    @Column(name = "main_balance_expired_date")
    private LocalDateTime mainBalanceExpiredDate;

    @Column(name = "promo_balance", precision = 15, scale = 2, columnDefinition = "DECIMAL(15,2) DEFAULT 0.00")
    private BigDecimal promoBalance = BigDecimal.ZERO;

    @Column(name = "promo_balance_expired_date")
    private LocalDateTime promoBalanceExpiredDate;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedAt;
}
