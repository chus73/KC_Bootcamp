package com.kc.news.crawler;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AlgoliaHitDto {

	@JsonProperty("Title")
	private String title;	
	
	@JsonProperty("Summary")
	private String summary;
	
	@JsonProperty("FullUrl")
	private String fullUrl;
	
	@JsonProperty("ImageUrl")
	private String imageUrl;
	
	@JsonProperty("PublicationTime")
	private Long date;
		
	@JsonProperty("AuthorName")
	private String author;
	
	@JsonProperty("CompanyName")
	private String company;
		
	@JsonProperty("Category")
	private String category;
	
	@JsonProperty("BusinessId")
	private String businessId;
	
	@JsonProperty("Tags")
	private List<String> tags;
	
	@JsonProperty("Article")
	private String article;
	
	@JsonProperty("objectID")
	private String objectId;
	
	@JsonProperty("CultureName")
	private String culture;
	
	private String dateFormatted;
}
