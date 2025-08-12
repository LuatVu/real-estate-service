package com.realestate.services;

import com.realestate.dto.UserDto;
import com.realestate.models.User;
import com.realestate.dto.ChangePasswordDto;

public interface UserService {
    boolean existByUsername(String username);
    boolean existByEmail(String email);
    boolean existByPhoneNumber(String phoneNumber);
    void save(User user);
    void checkAndCreateUser(UserDto userDto) throws Exception;
    UserDto getUserById(String userId);
    void updateUser(UserDto userDto) throws Exception;
    void changePassword(ChangePasswordDto changePasswordDto) throws Exception;
}
