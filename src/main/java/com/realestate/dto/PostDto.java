package com.realestate.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto implements Serializable{
    private String postId; 
    private String userId;
    private String title;   
    private String description;
    private BigDecimal acreage;
    private Integer bedrooms;
    private Integer bathrooms;
    private String furniture;
    private String legal;
    private BigDecimal price;    
    private String provinceCode;
    private String districtCode;
    private String wardCode;
    private String address;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime expiredAt;
    private String status;
    private Integer floors;
    private String direction;
    private String type;
    private List<ImagesDto> images;
    private RankingDto rankingDto;
    
}
