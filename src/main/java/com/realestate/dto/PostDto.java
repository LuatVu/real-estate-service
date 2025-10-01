package com.realestate.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto implements Serializable{
    private String postId; 
    private String userId;
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Description is required")
    private String description;
    @NotNull(message="Acreage is required")
    private BigDecimal acreage;
    private Integer bedrooms;
    private Integer bathrooms;
    private String furniture;
    private String legal;
    @NotNull(message="Price is required")
    private BigDecimal price;
    @NotBlank(message="Province code is required")
    private String provinceCode;
    private String districtCode;
    @NotBlank(message="Ward code is required")
    private String wardCode;
    private String address;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime expiredAt;
    private String status;
    private Integer floors;
    private Integer frontage;
    private String direction;
    @NotBlank(message="Type is required")
    private String type;
    private List<ImagesDto> images;
    @NotNull(message="Ranking is required")
    private RankingDto rankingDto;
    private String transactionType;
    private UserDto user;
}
