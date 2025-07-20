package com.realestate.dto;



import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignUpRequestDto implements Serializable{
    // @NotBlank(message = "Username is required!")
    @Size(min= 3, message = "Username must have atleast 3 characters!")
    @Size(max= 30, message = "Username can have have atmost 30 characters!")
    private String username;

    @Email(message = "Email is not in valid format!")
    // @NotBlank(message = "Email is required!")
    private String email;

    @NotBlank(message = "Phone number is required!")
    @Pattern(
        regexp = "^(\\+84|84|0)(3[2-9]|5[689]|7[06-9]|8[1-9]|9[0-46-9])[0-9]{7}$",
        message = "Số điện thoại phải là một số điện thoại Việt Nam hợp lệ (e.g., 0987654321, +84987654321)"
    )
    private String phoneNumber;

    @NotBlank(message = "Password is required!")
    @Size(min = 8, message = "Password must have atleast 8 characters!")
    @Size(max = 20, message = "Password can have have atmost 20 characters!")
    private String password;    
}
