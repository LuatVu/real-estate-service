package com.realestate.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInRequestDto implements Serializable{
    private String email;    
    private String phoneNumber;
    @NotBlank(message = "Password is required!")
    private String password;
}
