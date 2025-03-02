package com.realestate.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImagesDto implements Serializable{
    private String imageId;
    private String postId;
    private String filePath;
    private String fileName;
    private Integer fileSize;
    private String mimeType;
    private Boolean isPrimary;
    private LocalDateTime uploadDate;
}
