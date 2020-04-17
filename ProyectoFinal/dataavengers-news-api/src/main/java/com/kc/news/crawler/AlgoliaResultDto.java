package com.kc.news.crawler;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AlgoliaResultDto {

	@JsonProperty("hits")
	private List<AlgoliaHitDto> hits = new ArrayList<>();
	
	@JsonProperty("nbHits")
	private Integer nbHits;
	
	@JsonProperty("page")
	private Integer page;
	
	@JsonProperty("nbPages")
	private Integer nbPages;
	
	@JsonProperty("hitsPerPage")
	private Integer hitsPerPage;
	
	@JsonProperty("exhaustiveFacetsCount")
	private Boolean exhaustiveFacetsCount;
	
	@JsonProperty("exhaustiveNbHits")
	private Boolean exhaustiveNbHits;
	
	@JsonProperty("query")
	private String query;
	
	@JsonProperty("params")
	private String params;
	
	@JsonProperty("index")
	private String index;
}