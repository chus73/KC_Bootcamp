package com.kc.twitter.model.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class TwitterDto {

	@JsonProperty("id")
    private Long id;
    
    @JsonProperty("id_str")
    private String idStr;   
    
    @JsonProperty("retweet_count")
    private Integer retweetCount;  
    
    @JsonProperty("favorite_count")
    private Integer favoriteCount;  
    
    @JsonProperty("favorited")
    private Boolean favorited;  
    
    @JsonProperty("retweeted")
    private Boolean retweeted;     
    
    @JsonProperty("text")
    private String text;   
    
    @JsonProperty("created_at")
    private String createdAt; 
    
    @JsonProperty("lang")
    private String language;   
     
    private String dateFormatted;
    private String sourceQuery;
    private List<String> hashtags = new ArrayList<>();
    
}