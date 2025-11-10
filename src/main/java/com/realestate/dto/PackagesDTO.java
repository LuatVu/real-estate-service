package com.realestate.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PackagesDTO implements Serializable {
    private String packageId;
    private String packageName;
    private String packageDescription;
    private Integer diamondPosts;
    private Integer goldPosts;
    private Integer silverPosts;
    private Integer normalPosts;
    private Double price;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String image;
}
