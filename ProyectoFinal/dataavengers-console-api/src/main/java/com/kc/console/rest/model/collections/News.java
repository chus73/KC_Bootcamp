package com.kc.console.rest.model.collections;

import java.io.Serializable;
import java.util.List;

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
@Document(collection = "news")
public class News implements Serializable {
	
	@Id
	@JsonProperty("_id")
	public ObjectId collectionId;
	
	private String dateFormatted;	
	private String Summary;
	private String FullUrl;
	private String PublicationTime;
	private String AuthorName;
	private List<String> Tags;
	private String Article;
	private String objectID;

}
