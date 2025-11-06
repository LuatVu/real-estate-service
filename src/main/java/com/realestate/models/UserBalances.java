package com.realestate.models;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Data
@Entity
@Getter
@Builder
@Table(name = "user_balances")
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class UserBalances {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "balance_id")
    private String balanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "balance")
    private Double balance;    

    @Column(name = "expired_date")
    private LocalDateTime expiredDate;    

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "balance_type")
    private BalanceType balanceType;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private Status status;

    public enum BalanceType {
        MAIN,
        PROMO
    }

    public enum Status {
        ACTIVE,
        INACTIVE,
        EXPIRED
    }
}
