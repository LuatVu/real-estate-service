package com.realestate.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Data
@Builder
@Getter
@Setter
public class ApiResponseDto<T> implements Serializable{
    private String status;
    private String message;
    private T response;
}
