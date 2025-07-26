package com.realestate.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private String userId;
    private String username;
    private String email;
    private String phoneNumber;
    private String googleId;
    private String facebookId;
    private String authProvider; // e.g., "Credentials", "Google", "Facebook"
}
