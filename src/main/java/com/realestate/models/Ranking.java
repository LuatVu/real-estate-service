package com.realestate.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Data
@Entity
@Table(name = "ranking")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ranking implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ranking_id")    
    private String rankingId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore
    private Posts post;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority_level", nullable = false)
    private PriorityLevel priorityLevel;

    @Column(name = "bump_time")
    private LocalDateTime bumpTime;

    @PrePersist
    protected void onCreate() {
        bumpTime = LocalDateTime.now();
    }

    public enum PriorityLevel {
        DIAMOND(4),   // Highest priority
        GOLD(3),
        SILVER(2),
        NORMAL(1);    // Lowest priority

        private final int priority;

        PriorityLevel(int priority){
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }
    }
}
