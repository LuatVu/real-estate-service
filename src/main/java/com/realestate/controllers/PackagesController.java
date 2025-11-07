package com.realestate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.dto.ApiResponseDto;
import com.realestate.services.PackagesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


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
    
}
