package com.realestate.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class UserBalancesDTO {    
    private String userId;
    private Double mainBalance;
    private Double promoBalance;
    private LocalDateTime mainBalanceExpiredDate;
    private LocalDateTime promoBalanceExpiredDate;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
