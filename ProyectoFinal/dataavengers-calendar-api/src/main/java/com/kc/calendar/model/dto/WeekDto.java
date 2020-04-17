package com.kc.calendar.model.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeekDto {
	
	private String startDateFormatted;
	private String endDateFormatted;
	private Date startDate;
	private Date endDate;
	private Integer weekOfYear;
	private Integer startYear;
	private Integer endYear;

}
