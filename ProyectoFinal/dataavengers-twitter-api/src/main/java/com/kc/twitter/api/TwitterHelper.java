package com.kc.twitter.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kc.twitter.model.dto.TwitterDto;
import com.kc.twitter.model.entities.TwitterEntity;
import com.kc.twitter.model.repositories.TwitterRepository;

@Component
public class TwitterHelper {

	Logger logger = LoggerFactory.getLogger(TwitterHelper.class);

	public final static ObjectMapper MAPPER = new ObjectMapper();
	
	private File file;
	
	/* Search */

	@Value("${api.twitter.url.search}")
	private String apiSearch;

	@Value("${api.twitter.url.search.result_type}")
	private String apiSearchType;

	@Value("${api.twitter.url.search.count}")
	private int apiSearchCount;

	@Value("${api.twitter.url.search.names}")
	private List<String> apiSearchNames;

	@Value("${api.twitter.url.search.hashtags}")
	private List<String> apiSearchHashtags;
	
	/* Timeline */

	@Value("${api.twitter.url.timeline}")
	private String apiTimeline;
	
	@Value("${api.twitter.url.timeline.screen_names}")
	private List<String> apiTimelineNames;
	
	@Value("${api.twitter.url.timeline.count}")
	private int apiTimelineCount;
	
	/* Common */
	
	@Value("${api.file.export.path}")
	private String filePath;
	
	@Value("${api.twitter.date.input}")
	private String apiDateFormatInput;

	@Value("${api.twitter.date.output}")
	private String apiDateFormatOutput;
	
	@Value("${app.enable.toDatabase}")
	private Boolean enabledToDatabase;
	
	@Value("${app.enable.toFile}")
	private Boolean enabledToFile;
	
	@Value("${app.enable.toKafka}")
	private Boolean enabledToKafka;

	@Autowired
	private TwitterRepository twitterRepository;
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	 	
	@PostConstruct
	public void setUp() {
		file = new File(filePath);
	}
	
	/**
	 * /statuses/user_timeline.json
	 * 
	 * @return
	 */
	public Map<String, String> buildTimelinePaths() {

		Map<String, String> queries = new LinkedHashMap<>();

		for (String name : apiTimelineNames) {
			queries.put(name, buildTimelinePath(name));
		}

		return queries;
	}
	
	private String buildTimelinePath(String screenName) {
		Map<String, String> params = new HashMap<>();
		params.put("screen_name", encodeValue(screenName));
		params.put("exclude_replies", "true");
		params.put("count", String.valueOf(apiTimelineCount));
		return buildParamsPath(apiTimeline, params);
	}
	
	/**
	 * /search/tweets.json
	 * 
	 * @return
	 */
	public Map<String, String> buildSearchPaths() {

		Map<String, String> queries = new LinkedHashMap<>();

		for (String name : apiSearchNames) {
			queries.put(name, buildSearchPath(name));
		}

		for (String name : apiSearchHashtags) {
			queries.put(name, buildSearchPath(name));
		}

		return queries;
	}

	private String buildSearchPath(String qValue) {
		Map<String, String> params = new HashMap<>();
		params.put("q", encodeValue(qValue));
		params.put("result_type", apiSearchType);
		params.put("count", String.valueOf(apiSearchCount));
		return buildParamsPath(apiSearch, params);
	}

	private String buildParamsPath(String path, Map<String, String> params) {

		StringBuilder builder = new StringBuilder(path);

		if (!params.isEmpty()) {
			builder.append("?");
		}

		for (Entry<String, String> entry : params.entrySet()) {
			builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}

		if (!params.isEmpty())
			return builder.toString().substring(0, builder.lastIndexOf("&"));
		else
			return builder.toString();
	}

	public List<TwitterDto> buildInfoFromSearchResponse(String source, String result) {

		List<TwitterDto> dtos = new LinkedList<TwitterDto>();

		try {

			JSONObject response = new JSONObject(result);
			JSONArray statuses = response.getJSONArray("statuses");

			for (int i = 0; i < statuses.length(); i++) {

				try {

					JSONObject tweet = statuses.getJSONObject(i);
					List<String> hashtagsFinal = new ArrayList<String>();

					try {

						JSONObject tweetEntities = tweet.getJSONObject("entities");
						JSONArray hashTags = tweetEntities.getJSONArray("hashtags");
						for (int j = 0; j < hashTags.length(); j++) {
							JSONObject hashTagJson = hashTags.getJSONObject(j);
							String hastagString = "#" + hashTagJson.getString("text");
							hashtagsFinal.add(hastagString);
						}

					} catch (Exception e) {
						logger.error("Error getting tags: {}", e.getMessage());
					}

					TwitterDto dto = MAPPER.readValue(tweet.toString(), TwitterDto.class);
					dto.setSourceQuery(source);
					dto.setHashtags(hashtagsFinal);
					dto.setDateFormatted(formatDate(dto.getCreatedAt()));
					dtos.add(dto);

				} catch (Exception e) {
					logger.error("Error getting tweet: {}", e.getMessage());
				}

			}

		} catch (Exception e) {
			logger.error("Error getting statuses: {}", e.getMessage());
		}

		return dtos;
	}
	
	public List<TwitterDto> buildInfoFromTimelineResponse(String source, String result) {

		List<TwitterDto> dtos = new LinkedList<TwitterDto>();

		try {

			JSONArray response = new JSONArray(result);
	
			for (int i = 0; i < response.length(); i++) {

				try {

					JSONObject tweet = response.getJSONObject(i);
					List<String> hashtagsFinal = new ArrayList<String>();

					try {

						JSONObject tweetEntities = tweet.getJSONObject("entities");
						JSONArray hashTags = tweetEntities.getJSONArray("hashtags");
						for (int j = 0; j < hashTags.length(); j++) {
							JSONObject hashTagJson = hashTags.getJSONObject(j);
							String hastagString = "#" + hashTagJson.getString("text");
							hashtagsFinal.add(hastagString);
						}

					} catch (Exception e) {
						logger.error("Error getting tags: {}", e.getMessage());
					}

					TwitterDto dto = MAPPER.readValue(tweet.toString(), TwitterDto.class);
					dto.setSourceQuery(source);
					dto.setHashtags(hashtagsFinal);
					dto.setDateFormatted(formatDate(dto.getCreatedAt()));
					dtos.add(dto);

				} catch (Exception e) {
					logger.error("Error getting tweet: {}", e.getMessage());
				}

			}

		} catch (Exception e) {
			logger.error("Error getting response: {}", e.getMessage());
		}

		return dtos;
	}
	
	/*
	 * ------------------------------------
	 */
	
	private String encodeValue(String value) {
		try {
			return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			return value;
		}
	}

	private String formatDate(String twitterDate) {

		SimpleDateFormat sdfSource = new SimpleDateFormat(apiDateFormatInput, Locale.ENGLISH);
		SimpleDateFormat sdfTarget = new SimpleDateFormat(apiDateFormatOutput, Locale.ENGLISH);

		try {
			Date date = sdfSource.parse(twitterDate);
			return sdfTarget.format(date);
		} catch (ParseException e) {
			return twitterDate;
		}
	}

	public String parse(TwitterDto dto) {
		try {
			return TwitterHelper.MAPPER.writeValueAsString(dto);		
		} catch (Exception e) {
			logger.error("Error parsing json: {}", e.getMessage());
		}
		return "";
	}
	
	public boolean toFile(TwitterDto dto, String json) {
		
		boolean saved = false;
		if(enabledToFile) {
			try {
				if(!findInFile(dto.getIdStr())) {
					
					try {
						FileUtils.writeStringToFile(file, json+ "\r\n", StandardCharsets.UTF_8, true);
						saved = true;
					} catch (IOException e) {
						logger.error("Error writing to file: {}", e.getMessage());
					}
					
				} else {
					logger.debug("Repeated in File! {}", dto.getId());
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
	
	public boolean toDatabase(TwitterDto dto, String json) {
		
		boolean stored = false;
		if(enabledToDatabase) {
			try {
				Optional<TwitterEntity> found = twitterRepository.findById(dto.getId());						
				if(!found.isPresent()) {
					
					TwitterEntity entity = new TwitterEntity(
							dto.getId(),
							dto.getSourceQuery(), json);
				
					twitterRepository.save(entity);
					stored = true;
					
				} else {
					logger.debug("Repeated in DB! {}", dto.getId());
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
