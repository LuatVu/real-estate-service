package com.realestate.dto;

import lombok.Data;

@Data
public class PostRequest {
    private String title;
    private String transactionType;
    private Long lastDate;
}
