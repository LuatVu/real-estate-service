package com.realestate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.models.Role;

public interface RoleRepository extends JpaRepository<Role, String>{
    Role findByRoleName(String name);
}
