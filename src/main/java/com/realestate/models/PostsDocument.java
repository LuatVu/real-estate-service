package com.realestate.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.realestate.utilities.MessageUtils;
import com.realestate.utilities.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(indexName = "posts", writeTypeHint = WriteTypeHint.FALSE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PostsDocument {
    @Id    
    private String postId;
    
    @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String title;
    private String userId;
    private String username;
    private String phoneNumber;
    private String email;

    @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String description;
    private BigDecimal acreage;
    private Integer bedrooms;
    private Integer bathrooms;

    @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String furniture;

    @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String legal;
    
    @Field(type=FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String address;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime expiredAt;
    private LocalDateTime bumpTime;
    private Integer floors;

    @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String direction;

    @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String type;
    private String typeCode;

    @Field(type = FieldType.Text, analyzer = "custom_vn_analyzer")
    private String province;
    private String provinceCode;
    @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String district;
    private String districtCode;
    @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer")
    private String ward;
    private String wardCode;

    private String priorityLevel;

    @Field(type=FieldType.Integer)
    private Integer priorityLevelValue;

    private String status;    

    @Field(type=FieldType.Text)
    @JsonIgnore
    private String keywords;

    @Field(type=FieldType.Text)
    private String imageUrl;

    @Field(type=FieldType.Text)
    private String imageName;


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
                                    .furniture(MessageUtils.getMessage(post.getFurniture()!=null?"furniture."+post.getFurniture().getValue():null))
                                    .legal(MessageUtils.getMessage(post.getLegal()!=null?"legal."+post.getLegal().getValue():null))
                                    .price(post.getPrice())
                                    .province(MessageUtils.getMessage(post.getProvinceCode()!=null?"province." + post.getProvinceCode():null))
                                    .provinceCode(post.getProvinceCode())
                                    .district(MessageUtils.getMessage(post.getDistrictCode()!=null?"district."+post.getDistrictCode():null))
                                    .districtCode(post.getDistrictCode())
                                    .ward(MessageUtils.getMessage(post.getWardCode()!= null?"ward."+post.getWardCode(): null))
                                    .wardCode(post.getWardCode())
                                    .address(post.getAddress())
                                    .createdDate(post.getCreatedDate())
                                    .expiredAt(post.getExpiredAt())                          
                                    .status(post.getStatus().toString())
                                    .floors(post.getFloors())
                                    .direction(MessageUtils.getMessage(post.getDirection()!=null?"direction."+post.getDirection().toString():null))
                                    .type(MessageUtils.getMessage(post.getType()!=null?"type."+post.getType().toString():null))
                                    .typeCode(post.getType().toString())
                                    .priorityLevel(post.getRanking().getPriorityLevel().toString())
                                    .priorityLevelValue(post.getRanking().getPriorityLevel().getPriority())
                                    .bumpTime(post.getRanking().getBumpTime())                                    
                                    .keywords(StringUtils.createKeyWords(
                                                post.getTitle(), post.getDescription(), 
                                                MessageUtils.getMessage("province." + post.getProvinceCode()),
                                                MessageUtils.getMessage("district."+post.getDistrictCode()),
                                                MessageUtils.getMessage("ward."+post.getWardCode()),
                                                post.getAddress(),
                                                post.getDirection() != null?MessageUtils.getMessage("direction."+post.getDirection().toString()):"",
                                                MessageUtils.getMessage("type."+post.getType().toString())
                                            ))
                                .build();

        Optional<Images> imgOpt = post.getImages().stream().filter(e -> e.getIsPrimary() != null && e.getIsPrimary()).findFirst();        
        if(imgOpt.isPresent()){
            Images img = imgOpt.get();
            document.setImageUrl(img.getFileUrl());
            document.setImageName(img.getFileName());
        }
        return document;
    }

    @JsonIgnore
    private String _class;
}
