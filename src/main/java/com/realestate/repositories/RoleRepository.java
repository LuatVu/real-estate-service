package com.realestate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.realestate.models.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String>{
    Role findByRoleName(String name);
}
