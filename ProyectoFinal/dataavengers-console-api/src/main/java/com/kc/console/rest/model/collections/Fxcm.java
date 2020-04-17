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
@Document(collection = "fxcm")
public class Fxcm implements Serializable {

	@Id
	@JsonProperty("_id")
	public ObjectId collectionId;
	
	private String instrument;
	private String periodId;
	private Long timestamp;
	private Double bidOpen;
	private Double bidClose;
	private Double bidHigh;
	private Double bidLow;
	private Double askOpen;
	private Double askClose;
	private Double askHigh;
	private Double askLow;
	private Long tickqty;
	private String dateFormatted;
}
