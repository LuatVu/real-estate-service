package com.realestate.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDto<T> implements Serializable{
    private String status;
    private String message;
    private T response;
}
