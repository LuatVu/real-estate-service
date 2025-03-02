package com.realestate.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignInRequestDto implements Serializable{
    private String email;    
    private String phoneNumber;
    @NotBlank(message = "Password is required!")
    private String password;
}
