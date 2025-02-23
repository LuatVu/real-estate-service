package com.realestate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realestate.exception.RoleNotFoundException;
import com.realestate.models.Role;
import com.realestate.repositories.RoleRepository;

@Component
public class RoleFactory {
    @Autowired
    RoleRepository roleRepository;

    public Role getInstance(String role) throws RoleNotFoundException {
        return roleRepository.findByRoleName(role);        
    }
}
