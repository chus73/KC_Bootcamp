package com.kc.console.rest.business.dto;

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
public class ChartReport {

	@JsonProperty("_id")	
	public String id;
	
	public Long total;
}
