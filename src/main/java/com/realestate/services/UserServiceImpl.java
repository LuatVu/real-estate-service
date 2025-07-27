package com.realestate.services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.realestate.models.Role;
import com.realestate.models.User;
import com.realestate.repositories.UserRepository;
import com.realestate.dto.UserDto;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Autowired
    private RoleFactory roleFactory;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean existByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    @Override
    public boolean existByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void checkAndCreateUser(UserDto userDto) throws Exception{
        // validate the userDto fields
        if(userDto.getAuthProvider() == null || userDto.getAuthProvider().isEmpty()) {
            throw new IllegalArgumentException("AuthProvider must be specified.");
        }
        if(userDto.getUsername() == null || userDto.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username must be specified.");
        }
        if(userDto.getAuthProvider() == "Google" && (userDto.getGoogleId() == null || userDto.getGoogleId().isEmpty())) {
            throw new IllegalArgumentException("Google ID must be specified for Google authentication.");
        }
        if(userDto.getAuthProvider() == "Facebook" && (userDto.getFacebookId() == null || userDto.getFacebookId().isEmpty())) {
            throw new IllegalArgumentException("Facebook ID must be specified for Facebook authentication.");
        }
        // Check if user exists by email or phone number
        if(userDto.getAuthProvider() == "Google" && userRepository.findByGoogleId(userDto.getGoogleId()).isPresent()){
            // User already exists with Google ID, handle accordingly
            throw new RuntimeException("User already exists with the provided Google ID.");

        }else if(userDto.getAuthProvider() == "Facebook" && userRepository.findByFacebookId(userDto.getFacebookId()).isPresent()){
            // User already exists with Facebook ID, handle accordingly
            throw new RuntimeException("User already exists with the provided Facebook ID.");
        }

        Set<Role> roles = new HashSet<Role>();
        roles.add(roleFactory.getInstance("SUBSCRIBER"));

        // Create a new User entity from UserDto
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setGoogleId(userDto.getGoogleId());
        user.setRoles(roles);
        user.setFacebookId(userDto.getFacebookId());
        user.setAuthProvider("Google".equals(userDto.getAuthProvider()) ? User.AuthProvider.Google :
                            "Facebook".equals(userDto.getAuthProvider()) ? User.AuthProvider.Facebook :
                            User.AuthProvider.Credentials);
        // Save the new user
        save(user);
    }
}
