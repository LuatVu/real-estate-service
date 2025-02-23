package com.realestate.services;

import org.springframework.http.ResponseEntity;

import com.realestate.dto.ApiResponseDto;
import com.realestate.dto.SignInRequestDto;
import com.realestate.dto.SignUpRequestDto;
import com.realestate.exception.RoleNotFoundException;
import com.realestate.exception.UserAlreadyExistsException;

public interface AuthService {
    ResponseEntity<ApiResponseDto<?>> signUp(SignUpRequestDto signUpRequestDto) throws UserAlreadyExistsException, RoleNotFoundException;
    ResponseEntity<ApiResponseDto<?>> signIn(SignInRequestDto request) throws Exception;
}
