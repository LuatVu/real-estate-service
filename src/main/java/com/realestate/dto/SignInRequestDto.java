package com.realestate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInRequestDto {
    private String email;    
    private String phoneNumber;
    @NotBlank(message = "Password is required!")
    private String password;
}
