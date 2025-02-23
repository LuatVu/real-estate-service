package com.realestate.services;

import com.realestate.models.User;

public interface UserService {
    boolean existByUsername(String username);
    boolean existByEmail(String email);
    void save(User user);
}
