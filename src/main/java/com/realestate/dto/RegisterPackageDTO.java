package com.realestate.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive; 
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterPackageDTO implements Serializable{
    @NotBlank(message = "User Id is required")
    private String userId;
    @NotBlank(message = "Package Id is required")
    private String packageId;
    @Positive(message = "Months is required and must be greater than 0")
    private Integer months;
    @Positive(message="Payment amount must be greater than 0")
    private Double paymentAmount;
}
