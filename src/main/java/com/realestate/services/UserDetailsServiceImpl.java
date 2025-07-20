package com.realestate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.realestate.dao.UserDetailsImpl;
import com.realestate.repositories.UserRepository;

import jakarta.transaction.Transactional;
import com.realestate.models.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String phonenumber) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(phonenumber)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with phone number: " + phonenumber));

        return UserDetailsImpl.build(user);
    }
}
