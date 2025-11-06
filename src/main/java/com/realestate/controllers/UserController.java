package com.realestate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.realestate.dto.ApiResponseDto;
import com.realestate.services.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import com.realestate.dto.ChangePasswordDto;
import com.realestate.dto.UserDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Define your user-related endpoints here
    @PostMapping("/check-and-create")
    public ResponseEntity<ApiResponseDto<?>> checkAndCreateUser(@RequestBody @Valid UserDto userDto) throws Exception {
        userService.checkAndCreateUser(userDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDto.builder()
                        .status(String.valueOf(HttpStatus.OK))
                        .response(userDto)
                        .build());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<?>> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok()
                .body(ApiResponseDto.builder()
                        .status(String.valueOf(HttpStatus.OK))
                        .response(userService.getUserById(userId))
                        .build());
    }
    
    @PostMapping("/update")
    public ResponseEntity<ApiResponseDto<?>> updateUser(@RequestBody @Valid UserDto userDto) throws Exception {
        userService.updateUser(userDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDto.builder()
                        .status(String.valueOf(HttpStatus.OK))                        
                        .build());
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponseDto<?>> changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto) throws Exception {
        userService.changePassword(changePasswordDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDto.builder()
                        .status(String.valueOf(HttpStatus.OK))
                        .build());
    }
    

    @GetMapping("/balances/{userId}")
    public ResponseEntity<ApiResponseDto<?>> getUserBalances(@PathVariable String userId) {
        try{
            return ResponseEntity.ok()
                    .body(ApiResponseDto.builder()
                            .status(String.valueOf(HttpStatus.OK))
                            .response(userService.getUserBalances(userId))
                            .build());
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }        
    }
    
}
