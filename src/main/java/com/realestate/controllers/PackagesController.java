package com.realestate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.dao.UserDetailsImpl;
import com.realestate.dto.ApiResponseDto;
import com.realestate.dto.RegisterPackageDTO;
import com.realestate.dto.UserPackagesDTO;
import com.realestate.services.PackagesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/packages")
public class PackagesController {
    @Autowired
    private PackagesService packagesService;

    @GetMapping("registerByUser/{userId}")
    public ResponseEntity<ApiResponseDto<?>> getUserPackages(@PathVariable String userId) {
        try{
            return ResponseEntity.ok()
                .body(ApiResponseDto.builder()
                        .status(String.valueOf(HttpStatus.OK))
                        .response(packagesService.getUserPackages(userId))
                        .build());
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<?>> registerPackages(@RequestBody @Valid RegisterPackageDTO data) {        
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            data.setUserId(userPrincipal.getId());

            UserPackagesDTO userPackagesDTO = packagesService.registerPackages(data);

            return ResponseEntity.ok(ApiResponseDto.builder()
                .status(String.valueOf(HttpStatus.OK))
                .message("Register package done!")
                .response(userPackagesDTO)
                .build());

        }catch(Exception e){
            ApiResponseDto<?> response = new ApiResponseDto<>("500", e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }    
    }
    
    
    @GetMapping("all")
    public ResponseEntity<ApiResponseDto<?>> getAllPackages() {
        try{
            return ResponseEntity.ok()
                .body(ApiResponseDto.builder()
                        .status(String.valueOf(HttpStatus.OK))
                        .response(packagesService.getAllPackages())
                        .build());
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    
}
