package com.realestate.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PostSearchRequest {
    private String query; // Free text search input
    
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal minAcreage;
    private BigDecimal maxAcreage;

    private String typeCode;
    private String provinceCode;
    private String districtCode;
    private String wardCode;
}
