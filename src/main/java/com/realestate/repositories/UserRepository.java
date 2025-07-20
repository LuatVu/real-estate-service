package com.realestate.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.realestate.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Boolean existsByUsername(String username);
    Boolean existsByPhoneNumber(String phoneNumber);
    Boolean existsByEmail(String email);
}
