package com.kc.fxcm.model.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class FxcmDto {

    private String instrumentId;    
    private String instrument;   
    private String periodId;   
    List<FxcmCandleDto> candles;
}