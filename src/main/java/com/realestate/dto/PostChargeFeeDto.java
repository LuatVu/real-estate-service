package com.realestate.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostChargeFeeDto {    
    private String postId;
    private String priorityLevel;
    private Double reupFee;
    private Double renewFee;
    private Integer status;
}
