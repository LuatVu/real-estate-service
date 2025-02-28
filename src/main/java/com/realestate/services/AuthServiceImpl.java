package com.realestate.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.realestate.dao.UserDetailsImpl;
import com.realestate.dto.ApiResponseDto;
import com.realestate.dto.SignInRequestDto;
import com.realestate.dto.SignInResponseDto;
import com.realestate.dto.SignUpRequestDto;
import com.realestate.exception.RoleNotFoundException;
import com.realestate.exception.UserAlreadyExistsException;
import com.realestate.models.Role;
import com.realestate.models.User;
import com.realestate.security.JWTProvider;

@Service
public class AuthServiceImpl implements AuthService{
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleFactory roleFactory;

    @Autowired
    private JWTProvider jwtProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public ResponseEntity<ApiResponseDto<?>> signUp(SignUpRequestDto signUpRequestDto)
            throws UserAlreadyExistsException, RoleNotFoundException {
        // (1)
        if (userService.existByEmail(signUpRequestDto.getEmail())) {
            throw new UserAlreadyExistsException("Registration Failed: Provided email already exists. Try sign in or provide another email.");
        }
        if (userService.existByUsername(signUpRequestDto.getUsername())) {
            throw new UserAlreadyExistsException("Registration Failed: Provided username already exists. Try sign in or provide another username.");
        }
        // (2)
        User user = createUser(signUpRequestDto);
        // (3)
        userService.save(user);
        // (4)
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseDto.builder()
                        .status(String.valueOf(HttpStatus.OK))
                        .message("User account has been successfully created!")
                        .build()
        );
    }

    private User createUser(SignUpRequestDto signUpRequestDto) throws RoleNotFoundException {
        Set<Role> roles = new HashSet<Role>();
        roles.add(roleFactory.getInstance("SUBSCRIBER"));

        return User.builder()
                .email(signUpRequestDto.getEmail())
                .username(signUpRequestDto.getUsername())
                .passwordHash(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .isActive(true)
                .roles(roles)
                .build();
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> signIn(SignInRequestDto signInRequestDto) throws Exception{
        // (1)        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequestDto.getEmail(), signInRequestDto.getPassword()));        
        
        // (2)
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // (3)
        String jwt = jwtProvider.generateJwtToken(authentication);
        // (4)
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // (5)
        // List<String> roles = userDetails.getAuthorities().stream()
        //         .map(GrantedAuthority::getAuthority)
        //         .collect(Collectors.toList());

        // (6)
        SignInResponseDto signInResponseDto = SignInResponseDto.builder()
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .id(userDetails.getId())
                .token(jwt)
                .type("Bearer")
                // .roles(roles)
                .permissions(userDetails.getPermissions())
                .build();

        return ResponseEntity.ok(
                ApiResponseDto.builder()
                        .status(String.valueOf(HttpStatus.OK))
                        .message("Sign in successfull!")
                        .response(signInResponseDto)
                        .build()
        );
    }
}
