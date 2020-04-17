package com.kc.fxcm.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kc.fxcm.api.FxcmHelper;
import com.kc.fxcm.api.HttpHelper;
import com.kc.fxcm.connect.FxcmApiRepository;
import com.kc.fxcm.connect.events.FxcmEventResetInstruments;
import com.kc.fxcm.connect.events.FxcmEventUpdateInstruments;
import com.kc.fxcm.model.dto.FxcmDto;
import com.kc.fxcm.model.dto.InstrumentDto;

@Component
public class FxcmTasks {

	Logger logger = LoggerFactory.getLogger(FxcmTasks.class);

	@Autowired
	private HttpHelper httpUtil;

	@Autowired
	private FxcmHelper fxcmUtil;

	@Autowired
	private FxcmApiRepository fxcmApiRepository;
	
	@Autowired
    private ApplicationEventPublisher eventPublisher;	
	
	@Scheduled(initialDelay = 20000, fixedRateString = "${api.fxcm.get.historical.scheduled.rate}")
	public void instruments() throws InterruptedException {
		
		if(fxcmApiRepository.isConnected()) {
			instrumentsAction();			
		}	
	}
	
	private void instrumentsAction() {
		
		logger.info(" --------- start instrumentsAction() ----------- ");
		List<InstrumentDto> instruments = fxcmApiRepository.getInstrumentsCache();			
		logger.info("instruments {}", instruments);
		
		for(InstrumentDto instrument : instruments) {
			
			String pathHistorical = fxcmUtil.buildHistoricalPath(instrument.getOfferId());				
			String historicalResult = httpUtil.get(pathHistorical, fxcmApiRepository.getBearerToken());
			
			logger.debug("historicalResult for instrument: {}", historicalResult);
			FxcmDto result = fxcmUtil.buildInfoFromHistoricalResult(instrument.getInstrument(), historicalResult);
			
			logger.info("Processed <{}> candles for instrument <{}>:<{}> in Period: <{}>", 
					result.getCandles().size(), result.getInstrument(), result.getInstrumentId(), result.getPeriodId());
			
			String json = fxcmUtil.parse(result);					
			boolean stored = fxcmUtil.toDatabase(result, json);					
			boolean saved  = fxcmUtil.toFile(result, json);		
			
			boolean sent = false;
			if(fxcmUtil.shouldSendToKafka(stored, saved)) {
				sent  = fxcmUtil.toKafka(json);
			}
			
			logger.debug("Stored in DB: {}", stored);
			logger.debug("Saved in file: {}", saved);
			logger.debug("Sent to Kafka: {}", sent);					
		}
		logger.info(" --------- end instrumentsAction() ----------- ");
	}
	
	/**
	 * Update info to store: Symbol / offerId / Visibility for each required instrument
	 * 
	 * @param event
	 */
	@EventListener
	public void fxcmEventUpdateSymbolsHandler(FxcmEventUpdateInstruments event) {			
		
		logger.info("FxcmEventUpdateInstruments");
		HashMap<String, Boolean> instrumentsMap = new HashMap<>();
		
		String path2 = fxcmUtil.buildGetInstrumentsPath();
		String jsonString = httpUtil.get(path2, fxcmApiRepository.getBearerToken());
		JSONObject root = new JSONObject(jsonString);			
		JSONArray instrumentsJson = root.getJSONObject("data").getJSONArray("instrument");			
		
		logger.info("Getting visibility...");
		for(int i=0; i < instrumentsJson.length(); i++) {
			JSONObject instrument = instrumentsJson.getJSONObject(i);
			String symbol = instrument.getString("symbol");
			boolean isVisible = instrument.getBoolean("visible");				
			logger.debug("Symbol: <{}>:<{}>", symbol, isVisible);				
			instrumentsMap.put(symbol, isVisible);
		}
		
		logger.info("Checking visibility and getting offerId...");
		List<InstrumentDto> instrumentsFinal = new ArrayList<>();
		
		boolean shouldReset = false;
		for(String instrumentRequested : fxcmUtil.getInstrumentsSearched()) {			
			
			boolean isVisible = instrumentsMap.getOrDefault(instrumentRequested, false);			
			Integer offerId = fxcmUtil.offerIdFromInstrument(instrumentRequested);
			
			if(!isVisible || offerId == -1) {
				logger.info("Reset is neccesary for <{}> offerId<{}>: isVisible<{}> ... ", instrumentRequested, offerId , isVisible);
				shouldReset = true;
				break;
				
			} else {
				instrumentsFinal.add(  
					InstrumentDto.builder()
						.instrument(instrumentRequested).offerId(offerId).visible(isVisible).build()
				);
			}
		}
		
		if(shouldReset) {
			eventPublisher.publishEvent(new FxcmEventResetInstruments(this));
		} else {
			logger.info("Configured instruments: {}", instrumentsFinal);
			fxcmApiRepository.setInstrumentsCache(instrumentsFinal);
		}			
	}
	
	/**
	 * Subscription must be updated to true
	 * 
	 * @param event
	 */
	@EventListener
	public void fxcmEventResetInstrumentsHandler(FxcmEventResetInstruments event) {	
		
		logger.info("FxcmEventResetInstruments");

		String path2 = fxcmUtil.buildGetInstrumentsPath();
		String jsonString = httpUtil.get(path2, fxcmApiRepository.getBearerToken());
		JSONObject root = new JSONObject(jsonString);			
		JSONArray instrumentsJson = root.getJSONObject("data").getJSONArray("instrument");			
		
		for(int i=0; i < instrumentsJson.length(); i++) {
			JSONObject instrument = instrumentsJson.getJSONObject(i);
			String symbol = instrument.getString("symbol");
			String params = fxcmUtil.buildPostUpdateSubscriptionsParams(symbol, false);
			httpUtil.post(fxcmUtil.getApiPostUpdateSubscriptions(), fxcmApiRepository.getBearerToken(), params);
		}
		
		for(int i=0; i < instrumentsJson.length(); i++) {
			JSONObject instrument = instrumentsJson.getJSONObject(i);
			String symbol = instrument.getString("symbol");
			String params = fxcmUtil.buildPostUpdateSubscriptionsParams(symbol, true);
			httpUtil.post(fxcmUtil.getApiPostUpdateSubscriptions(), fxcmApiRepository.getBearerToken(), params);
		}
		
		eventPublisher.publishEvent(new FxcmEventUpdateInstruments(this));
	}
	
}
