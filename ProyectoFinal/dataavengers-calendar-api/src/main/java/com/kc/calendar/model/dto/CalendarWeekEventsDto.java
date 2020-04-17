package com.kc.calendar.model.dto;

import java.util.LinkedList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalendarWeekEventsDto {
	
	private String startDateFormatted;
	private String endDateFormatted;
	private String id;	
	private List<CalendarEventDto> events = new LinkedList<>();

}
