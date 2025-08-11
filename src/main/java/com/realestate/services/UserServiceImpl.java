package com.realestate.services;

import java.util.HashSet;
import java.util.Set;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.realestate.models.Role;
import com.realestate.models.User;
import com.realestate.repositories.UserRepository;

import jakarta.transaction.Transactional;

import com.realestate.dto.UserDto;

@Service
public class UserServiceImpl implements UserService {
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
    @Transactional
    public void checkAndCreateUser(UserDto userDto) throws Exception {
        // validate the userDto fields
        if (userDto.getAuthProvider() == null || userDto.getAuthProvider().isEmpty()) {
            throw new IllegalArgumentException("AuthProvider must be specified.");
        }
        if (userDto.getUsername() == null || userDto.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username must be specified.");
        }
        if (userDto.getAuthProvider() == "google"
                && (userDto.getGoogleId() == null || userDto.getGoogleId().isEmpty())) {
            throw new IllegalArgumentException("Google ID must be specified for Google authentication.");
        }
        if (userDto.getAuthProvider() == "facebook"
                && (userDto.getFacebookId() == null || userDto.getFacebookId().isEmpty())) {
            throw new IllegalArgumentException("Facebook ID must be specified for Facebook authentication.");
        }
        // Check if user exists by email or phone number
        if (userDto.getAuthProvider() == "google" && userRepository.findByGoogleId(userDto.getGoogleId()).isPresent()) {
            // User already exists with Google ID, handle accordingly
            throw new RuntimeException("User already exists with the provided Google ID.");

        } else if (userDto.getAuthProvider() == "facebook"
                && userRepository.findByFacebookId(userDto.getFacebookId()).isPresent()) {
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
        user.setAuthProvider("google".equals(userDto.getAuthProvider()) ? User.AuthProvider.Google
                : "facebook".equals(userDto.getAuthProvider()) ? User.AuthProvider.Facebook
                        : User.AuthProvider.Credentials);
        user.setProfilePicture(userDto.getProfilePicture());
        // Save the new user
        save(user);
    }

    @Override
    public UserDto getUserById(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByGoogleId(userId);
        }

        // If still not found, try to find by FacebookId
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByFacebookId(userId);
        }
        return userOptional
            .map(user -> new UserDto(
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getGoogleId(),
                    user.getFacebookId(),
                    user.getAuthProvider().name(),
                    user.getAddress(),
                    user.getIdentificationCode(),
                    user.getTaxId(),
                    user.getProfilePicture()))
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    @Override
    @Transactional
    public void updateUser(UserDto userDto) throws Exception {
        // Validate the userDto fields
        if (userDto.getUserId() == null || userDto.getUserId().isEmpty()) {
            throw new IllegalArgumentException("User ID must be specified.");
        }
        if(userDto.getAuthProvider() == null || userDto.getAuthProvider().isEmpty()) {
            throw new IllegalArgumentException("AuthProvider must be specified.");
        }
        Optional<User> userOptional;
        if(User.AuthProvider.Google.toString().toLowerCase().equals(userDto.getAuthProvider().toLowerCase())  ){
            userOptional = userRepository.findByGoogleId(userDto.getUserId());
        }else if(User.AuthProvider.Facebook.toString().toLowerCase().equals(userDto.getAuthProvider().toLowerCase())){
            userOptional = userRepository.findByFacebookId(userDto.getUserId());
        }else{
            userOptional = userRepository.findById(userDto.getUserId());
        }
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userDto.getUserId());
        }

        User user = userOptional.get();
        user.setUsername(userDto.getUsername());
        // user.setEmail(userDto.getEmail()); //dont allow update user infor
        // user.setPhoneNumber(userDto.getPhoneNumber());
        // user.setGoogleId(userDto.getGoogleId());
        // user.setFacebookId(userDto.getFacebookId());
        user.setProfilePicture(userDto.getProfilePicture());
        user.setAddress(userDto.getAddress());
        user.setIdentificationCode(userDto.getIdentificationCode());
        user.setTaxId(userDto.getTaxId()); 

        userRepository.save(user);
    }
}
