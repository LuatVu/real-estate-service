package com.realestate.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignInResponseDto implements Serializable{
    private String token;    
    private String type = "Bearer";
    private String id;
    private String username;
    private String email;
    // private List<String> roles;
    private List<String> permissions;
}
