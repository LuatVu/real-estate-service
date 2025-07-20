package com.realestate.dto;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignInResponseDto implements Serializable{
    private String token;    
    private String type = "Bearer";
    private String id;
    private String username;
    private String email;
    private String phoneNumber;
    // private List<String> roles;
    private List<String> permissions;
}
