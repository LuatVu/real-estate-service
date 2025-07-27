package com.realestate.models;
import java.io.Serializable;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Data
@Entity
@Table(name = "roles")
public class Role implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "role_id")    
    private String roleId;

    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    // Many-to-Many relationship with User
    // @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)    
    // private Set<User> users;    

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions;
}