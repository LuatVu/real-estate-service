package com.realestate.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import com.realestate.models.Ranking.PriorityLevel;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Data
@Entity
@Table(name = "post_charge_fees")
public class PostChargeFees {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority_level", nullable = false)
    private PriorityLevel priorityLevel;

    @Column(name = "reup_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal reupFee;

    @Column(name = "renew_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal renewFee;

    @Column(name = "status", columnDefinition = "int default 1")
    private Integer status;
}
