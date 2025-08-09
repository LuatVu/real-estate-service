package com.realestate.services;

import com.realestate.dto.UserDto;
import com.realestate.models.User;

public interface UserService {
    boolean existByUsername(String username);
    boolean existByEmail(String email);
    boolean existByPhoneNumber(String phoneNumber);
    void save(User user);
    void checkAndCreateUser(UserDto userDto) throws Exception;
    UserDto getUserById(String userId);
}
