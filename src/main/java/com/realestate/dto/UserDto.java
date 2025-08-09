package com.realestate.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class UserDto {
    private String userId;
    private String username;
    private String email;
    private String phoneNumber;
    private String googleId;
    private String facebookId;
    private String authProvider; // e.g., "credentials", "google", "facebook"
    private String address;
    private String identificationCode;
    private String taxId;
    private String profilePicture;
}
