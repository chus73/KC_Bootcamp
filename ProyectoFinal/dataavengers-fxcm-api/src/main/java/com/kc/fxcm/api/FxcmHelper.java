package com.kc.fxcm.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import java.util.Optional;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kc.fxcm.connect.FxcmApiRepository;
import com.kc.fxcm.model.dto.FxcmCandleDto;
import com.kc.fxcm.model.dto.FxcmDto;
import com.kc.fxcm.model.entities.FxcmEntity;
import com.kc.fxcm.model.repositories.FxcmRepository;

@Component
public class FxcmHelper {
	
	Logger logger = LoggerFactory.getLogger(FxcmHelper.class);
	
	public final static ObjectMapper MAPPER = new ObjectMapper();
	
	private File file;
	
	@Value("${api.file.export.path}")
	private String filePath;

	@Value("${api.fxcm.post.update_subscriptions}")
	private String apiPostUpdateSubscriptions;
	
	@Value("${api.fxcm.get.model}")
	private String apiGetModel;

	@Value("${api.fxcm.get.instruments}")
	private String apiGetInstruments;
	
	@Value("${api.fxcm.get.historical}")
	private String apiGetHistorical;	
	
	@Value("${api.fxcm.get.historical.periodId}")
	private String periodId;
	
	@Value("${api.fxcm.get.historical.elements}")
	private String historicalNum;
	
	@Value("${api.fxcm.get.historical.from}")
	private String historicalFrom;
	
	@Value("${api.fxcm.get.historical.to}")
	private String historicalTo;
	
	@Value("${api.fxcm.get.historical.instruments}")
	private List<String> instrumentsSearched;
	
	@Value("${api.fxcm.get.historical.date_format}")
	private String dateFormat;
	
	@Value("${app.enable.toDatabase}")
	private Boolean enabledToDatabase;
	
	@Value("${app.enable.toFile}")
	private Boolean enabledToFile;
	
	@Value("${app.enable.toKafka}")
	private Boolean enabledToKafka;
		
	@Autowired
	private HttpHelper httpUtil;

	@Autowired
	private FxcmApiRepository tokenRepository;
	
	@Autowired
	private FxcmRepository fxcmRepository;
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@PostConstruct
	public void setUp() {
		file = new File(filePath);
	}
	
	public List<String> getInstrumentsSearched() {
		return instrumentsSearched;
	}
	
	/**
	 * /trading/update_subscriptions
	 * 
	 * @return
	 */
	public String buildPostUpdateSubscriptionsParams(String symbol, boolean visible) {	
		MultiValuedMap<String, String> params = new ArrayListValuedHashMap<>();
		params.put("symbol", symbol);
		params.put("visible", String.valueOf(visible));
		return buildParamsBody(params);
	}
	
	public String getApiPostUpdateSubscriptions() {
		return apiPostUpdateSubscriptions;
	}
	
	/**
	 * /trading/get_model/
	 * 
	 * @param params models one or more of: 'Offer', 'OpenPosition', 'ClosedPosition', 'Order', 
	 * 'Account', 'Summary', 'LeverageProfile', 'Properties'
	 * @return
	 */
	public String buildGetModelPath() {	
		MultiValuedMap<String, String> params = new ArrayListValuedHashMap<>();
		params.put("models", "Offer");
		params.put("models", "OpenPosition");
		params.put("models", "ClosedPosition");
		params.put("models", "Order");
		params.put("models", "Account");
		params.put("models", "Summary");
		params.put("models", "LeverageProfile");
		params.put("models", "Properties");
		return buildParamsPath(apiGetModel, params);
	}
	
	/**
	 * /trading/get_instruments
	 * 
	 * @return
	 */
	public String buildGetInstrumentsPath() {
		MultiValuedMap<String, String> params = new ArrayListValuedHashMap<>();
		return buildParamsPath(apiGetInstruments, params);
	}
	
	/**
	 * /candles/{offer_id}/{period_id}
	 * 
	 * @param offerId
	 * @param periodId
	 * @param params Possible params (num, from, to)
	 * @return
	 */
	public String buildHistoricalPath(Integer offerId) {
		
		MultiValuedMap<String, String> params = new ArrayListValuedHashMap<>();
				
		String apiHistoricalPath = apiGetHistorical
			.replace("{offer_id}", String.valueOf(offerId))
			.replace("{period_id}", periodId);
		
		if(!StringUtils.isEmpty(historicalNum)) {
			params.put("num", historicalNum);
		}
		
		if(!StringUtils.isEmpty(historicalFrom)) {
			params.put("from", historicalFrom);
		}
		
		if(!StringUtils.isEmpty(historicalTo)) {
			params.put("to", historicalTo);
		}
		
		return buildParamsPath(apiHistoricalPath, params);
	}
	
	/**
	 * For GET request
	 * 
	 * @param path
	 * @param params
	 * @return
	 */
	public String buildParamsPath(String path, MultiValuedMap<String, String> params) {
		
		StringBuilder builder = new StringBuilder(path);
		
		if(!params.isEmpty()) {
			builder.append("?");
		}
		
		for(Entry<String, Collection<String>> entry : params.asMap().entrySet()) {			
			for(String value : entry.getValue()) {
				builder.append(entry.getKey()).append("=").append(value).append("&");				
			}			
		}
		
		if(!params.isEmpty())
			return builder.toString().substring(0, builder.lastIndexOf("&"));
		else 
			return builder.toString();
	}
	
	/**
	 * For POST requests
	 * 
	 * @param params
	 * @return
	 */
	public String buildParamsBody(MultiValuedMap<String, String> params) {
		
		StringBuilder builder = new StringBuilder();

		for(Entry<String, Collection<String>> entry : params.asMap().entrySet()) {			
			for(String value : entry.getValue()) {
				builder.append(entry.getKey()).append("=").append(value).append("&");				
			}			
		}
		
		if(!params.isEmpty())
			return builder.toString().substring(0, builder.lastIndexOf("&"));
		else 
			return builder.toString();
	}
	
	public Integer offerIdFromInstrument(String instrument) {

		String path = buildGetModelPath();
		String jsonString = httpUtil.get(path, tokenRepository.getBearerToken());

		try {

			if (jsonString != null) {
				JSONObject jsonData = new JSONObject(jsonString);	
				JSONArray offers = jsonData.getJSONArray("offers");
				for (int i = 0; i < offers.length(); i++) {
					JSONObject offer = offers.getJSONObject(i);
					
					if(instrument.equalsIgnoreCase(offer.getString("currency"))) {
						Integer offerId = offer.getInt("offerId");
						logger.info("Found offerId: {} for instrument {}", offerId, instrument);
						return offerId;
					}
				}
			}
			
		} catch (Exception e) {
			logger.error("Error parsing Json response: {}", e.getMessage());
		}
		
		return -1;
	}
	

	public FxcmDto buildInfoFromHistoricalResult(String instrument, String historicalResult) {
		
		List<FxcmCandleDto> candles = new LinkedList<FxcmCandleDto>();
		FxcmDto dto = new FxcmDto();

		try {

			JSONObject response = new JSONObject(historicalResult);
			
			String instrumentId = response.getString("instrument_id");
			String periodId 	= response.getString("period_id");			
			JSONArray candlesArray   = response.getJSONArray("candles");
			
			dto.setInstrument(instrument);
			dto.setInstrumentId(instrumentId);
			dto.setPeriodId(periodId);
	
			for (int i = 0; i < candlesArray.length(); i++) {

				try {

					JSONArray candleInstance = candlesArray.getJSONArray(i);					
					FxcmCandleDto candle = FxcmCandleDto.buildCandle(candleInstance, dateFormat);
					candles.add(candle);
					
					logger.debug("candle {}", candle);

				} catch (Exception e) {
					logger.error("Error getting candle: {}", e.getMessage());
				}

			}

		} catch (Exception e) {
			logger.error("Error getting response: {}", e.getMessage());
		}
		
		dto.setCandles(candles);
		return dto;		
	}
	
	public String parse(FxcmDto dto) {
		try {
			return FxcmHelper.MAPPER.writeValueAsString(dto);		
		} catch (Exception e) {
			logger.error("Error parsing json: {}", e.getMessage());
		}
		return "";
	}
	
	public boolean toFile(FxcmDto dto, String json) {
		
		boolean saved = false;
		if(enabledToFile) {
			String databaseId = dto.getInstrumentId() + "_" + dto.getPeriodId();
			try {
				if(!findInFile(databaseId)) {
					
					try {
						FileUtils.writeStringToFile(file, json+ "\r\n", StandardCharsets.UTF_8, true);
						saved = true;
					} catch (IOException e) {
						logger.error("Error writing to file: {}", e.getMessage());
					}
					
				} else {
					logger.debug("Repeated in File! {}", databaseId);
				}
			} catch (Exception e) {
				logger.error("Error saving to file: {}", e.getMessage());
			}			
		}
		return saved;
	}
	
	private boolean findInFile(String id) throws Exception {
		
		String currentLine = null;

		try {

			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((currentLine = br.readLine()) != null) {
				if(currentLine.contains(id)) {
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	public boolean toDatabase(FxcmDto dto, String json) {
		
		boolean stored = false;
		if(enabledToDatabase) {
			
			String databaseId = dto.getInstrumentId() + "_" + dto.getPeriodId();
			
			try {
				Optional<FxcmEntity> found = fxcmRepository.findById(databaseId);						
				if(!found.isPresent()) {
					
					FxcmEntity entity = new FxcmEntity(
							databaseId,
							dto.getInstrument(), json);
				
					fxcmRepository.save(entity);
					stored = true;
					
				} else {
					logger.debug("Repeated in DB! {}", databaseId);
				}
				
			} catch (Exception e) {
				logger.error("Error saving to db: {}", e.getMessage());
			}		
		}	
		return stored;
	}
	
	public boolean shouldSendToKafka(boolean stored, boolean saved) {
		
		if(!enabledToFile && !enabledToDatabase && enabledToKafka) {
			return true;
		} else if((enabledToFile || enabledToDatabase) && (stored || saved) && enabledToKafka)  {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean toKafka(String json) {
		
		if(enabledToKafka) {
			kafkaTemplate.sendDefault(json);
			return true;
		} else {
			return false;
		}
	    
	}
}
