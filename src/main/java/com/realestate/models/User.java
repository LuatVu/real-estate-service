package com.realestate.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.LocalDateTime;

import java.util.Set;


@Data
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "phone_number"),
        @UniqueConstraint(columnNames = "email")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class User implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")    
    private String userId;

    @Column(name = "username", length = 50, unique = false)
    private String username;

    @Column(name = "phone_number", length = 15, unique = true)
    private String phoneNumber;

    @Column(name = "email", length = 100, unique = true)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

//     @Column(name = "account_balance", precision = 15, scale = 2)
//     private BigDecimal accountBalance = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // Many-to-Many relationship with Role
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })    
    @JsonIgnore
    @JoinTable(
            name = "user_roles", // Name of the join table
            joinColumns = @JoinColumn(name = "user_id"), // Foreign key for User
            inverseJoinColumns = @JoinColumn(name = "role_id") // Foreign key for Role
    )
    private Set<Role> roles;


    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JsonIgnore
    @JoinTable(
        name="user_packages",
        joinColumns = @JoinColumn(name="user_id"),
        inverseJoinColumns= @JoinColumn(name = "package_id")
    )
    private Set<Packages> packages;

    @Column(name = "google_id", length = 100, unique = true)
    private String googleId;

    @Column(name = "facebook_id", length = 100, unique = true)
    private String facebookId;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", length = 20, nullable = false)
    @Default private AuthProvider authProvider = AuthProvider.Credentials;

    public enum AuthProvider {
        Credentials,
        Google,
        Facebook
    }
}