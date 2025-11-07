package com.realestate.dto;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPackagesDTO implements Serializable {
    private String userPackageId;
    private String userId;
    private String packageId;
    private String packageName;
    private String packageDescription;
    private Integer remainingDiamondPosts;
    private Integer remainingGoldPosts;
    private Integer remainingSilverPosts;
    private Integer remainingNormalPosts;
    private LocalDateTime activeDate;
    private LocalDateTime expiredDate;
    private String status;
    private String image;
}
