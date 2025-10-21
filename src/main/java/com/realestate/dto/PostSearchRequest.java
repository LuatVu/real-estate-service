package com.realestate.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class PostSearchRequest {
    private String query; // Free text search input
    
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal minAcreage;
    private BigDecimal maxAcreage;

    private List<String> typeCodes;
    private String cityCode;    
    private List<String> wardCodes;
    private String transactionType;
}
