package com.kc.fxcm.model.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/*
 * Order:
 * 
 * timestamp, bidopen , bidclose, bidhigh, bidlow, askopen, askclose, askhigh, asklow, tickqty
 * 
 *     
 */
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class FxcmCandleDto {

    private Number timestamp;    
    private Number bidOpen;
    private Number bidClose;
    private Number bidHigh;
    private Number bidLow;
    private Number askOpen;
    private Number askClose;    
    private Number askHigh;
    private Number askLow;
    private Number tickqty;    
    
    private String dateFormatted;
        
    public static FxcmCandleDto buildCandle(
    		JSONArray candleInstance, 
    		String dateFormat) {
    	
    	FxcmCandleDto dto = new FxcmCandleDto();

    	Number[] result;
    	try {
    		result = new Number[candleInstance.length()];
        	for(int i=0; i < candleInstance.length(); i++) {	    	
        		result[i] = candleInstance.getNumber(i);
    	    }
    	} catch (Exception e) {
    		result = new Number[0];
    	}    	
    	
    	dto.setTimestamp( getResultOrDefault(result, 0) );
    	dto.setBidOpen( getResultOrDefault(result, 1) );
    	dto.setBidClose( getResultOrDefault(result, 2) );
    	dto.setBidHigh( getResultOrDefault(result, 3) );
    	dto.setBidLow( getResultOrDefault(result, 4) );
    	dto.setAskOpen( getResultOrDefault(result, 5) );
    	dto.setAskClose( getResultOrDefault(result, 6) );
    	dto.setAskHigh( getResultOrDefault(result, 7) );
    	dto.setAskLow( getResultOrDefault(result, 8) );
    	dto.setTickqty( getResultOrDefault(result, 9) );
    	
    	try {
    		Date date = new Date(dto.getTimestamp().longValue() * 1000);    		
    		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    		dto.setDateFormatted(sdf.format(date));
    	} catch (Exception e) {
    		dto.setDateFormatted("");
    	}
    	   	
    	return dto;
    }
    
    private static Number getResultOrDefault(Number[] result, int pos) {
    	
    	try {
    		return result[pos];
    	} catch(Exception e) {
    		return 0;
    	}
    }
}