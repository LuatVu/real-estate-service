package com.realestate.models;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permissions")
public class Permission implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "permission_id")    
    private String permissionId;
    
    @Column(name = "permission_name", length = 50, nullable = false, unique = true)
    private String permissionName;
    
    @Column(name = "description")
    private String description;
    
    // @ManyToMany(mappedBy = "permissions")
    // private Set<Role> roles;
}
