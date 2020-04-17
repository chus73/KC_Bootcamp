package com.kc.console.rest.model.collections;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
@Document(collection = "twitter")
public class Twitter implements Serializable {
	
	@Id
	@JsonProperty("_id")	
	public Long id;
	
	private String dateFormatted;
	private String sourceQuery;
	private List<String> hashtags;
	private String id_str;
	private Integer retweet_count;
	private Integer favorite_count;
	private Boolean favorited;
	private Boolean retweeted;
	private String text;
	private String created_at;
	private String lang;

}
