package com.realestate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
    Role findByRoleName(String name);
}
