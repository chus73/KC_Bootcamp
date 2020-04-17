package com.kc.fxcm.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstrumentDto {

	private String instrument;
	private Integer offerId;
	private Boolean visible;
}
