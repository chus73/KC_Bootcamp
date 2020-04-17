package com.kc.console.rest.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApiResponseErrorDto {
	
	private final int status;
	private final Object data;
	private final String description;
	private final String exceptionType;
	private final String exceptionMessage;

}
