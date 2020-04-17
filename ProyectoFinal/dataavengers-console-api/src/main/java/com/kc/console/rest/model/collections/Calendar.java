package com.kc.console.rest.model.collections;

import java.io.Serializable;

import org.bson.types.ObjectId;
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
@Document(collection = "calendar")
public class Calendar implements Serializable {

	@Id
	@JsonProperty("_id")
	public ObjectId collectionId;

	private String startDateFormatted;	
	private String endDateFormatted;
	private String id;
	private String hour;	
	private String currency;	
	private String event;	
	private String importance;	
	private String actual;	
	private String forecast;	
	private String previous;	
	private String rawEventDate;	
	private String eventDateFormatted;
}
