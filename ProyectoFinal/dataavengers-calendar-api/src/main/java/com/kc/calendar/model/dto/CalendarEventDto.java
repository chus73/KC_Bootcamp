package com.kc.calendar.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalendarEventDto {
	
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
