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
public class RankingDto implements Serializable{
    private String rankingId;
    private String postId;
    private String priorityLevel;
    private LocalDateTime bumpTime;
}
