package com.realestate.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ChangePasswordDto {
    private String userId;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
