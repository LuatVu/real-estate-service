package com.realestate.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.realestate.utilities.MessageUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(indexName = "posts", writeTypeHint = WriteTypeHint.FALSE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostsDocument {
    @Id    
    private String postId;
    
    // @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String title;
    private String userId;
    private String username;
    private String phoneNumber;
    private String email;

    // @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String description;
    private BigDecimal acreage;
    private Integer bedrooms;
    private Integer bathrooms;

    // @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String furniture;

    // @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String legal;
    
    @Field(type=FieldType.Double)
    private BigDecimal price;

    // @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String address;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime expiredAt;
    private Integer floors;

    // @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String direction;

    // @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String type;
    private String typeCode;

    // @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String province;
    private String provinceCode;
    // @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String district;
    private String districtCode;
    // @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String ward;
    private String wardCode;

    private String status;   

    public static PostsDocument fromEntity(Posts post){
        PostsDocument document = PostsDocument.builder()
                                    .postId(post.getPostId())
                                    .title(post.getTitle())
                                    .userId(post.getUser().getUserId())
                                    .username(post.getUser().getUsername())
                                    .phoneNumber(post.getUser().getPhoneNumber())
                                    .email(post.getUser().getEmail())
                                    .description(post.getDescription())
                                    .acreage(post.getAcreage())
                                    .bedrooms(post.getBedrooms())
                                    .bathrooms(post.getBathrooms())
                                    .furniture(MessageUtils.getMessage("furniture."+post.getFurniture().getValue()))
                                    .legal(MessageUtils.getMessage("legal."+post.getLegal().getValue()))
                                    .price(post.getPrice())
                                    .province(MessageUtils.getMessage("province." + post.getProvinceCode()))
                                    .provinceCode(post.getProvinceCode())
                                    .district(MessageUtils.getMessage("district."+post.getDistrictCode()))
                                    .districtCode(post.getDistrictCode())
                                    .ward(MessageUtils.getMessage("ward."+post.getWardCode()))
                                    .wardCode(post.getWardCode())
                                    .address(post.getAddress())
                                    .createdDate(post.getCreatedDate())
                                    .expiredAt(post.getExpiredAt())                          
                                    .status(post.getStatus().toString())
                                    .floors(post.getFloors())
                                    .direction(MessageUtils.getMessage("direction."+post.getDirection().toString()))
                                    .type(MessageUtils.getMessage("type."+post.getType().toString()))
                                    .typeCode(post.getType().toString())
                                .build();

        return document;
    }

    @JsonIgnore
    private String _class;
}
