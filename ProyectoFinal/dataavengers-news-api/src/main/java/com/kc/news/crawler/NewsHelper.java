package com.kc.news.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import com.kc.news.model.entities.NewsEntity;
import com.kc.news.model.repositories.NewsRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NewsHelper {

	private static final ObjectMapper JSON_MAPPER = new ObjectMapper()
			.registerModule(new JsonOrgModule())
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	private File file;
	
	@Value("${api.file.export.path}")
	private String filePath;

	@Value("${api.news.date.output}")
	private String apiDateFormatOutput;
	
	@Value("${app.enable.toDatabase}")
	private Boolean enabledToDatabase;
	
	@Value("${app.enable.toFile}")
	private Boolean enabledToFile;
	
	@Value("${app.enable.toKafka}")
	private Boolean enabledToKafka;
	
	@Autowired
	private NewsRepository newsRepository;
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@PostConstruct
	public void setUp() {
		file = new File(filePath);
	}
	
	private String formatDate(Long timestamp) {
		
		timestamp = timestamp != null ? timestamp * 1000 : 0;
		
		SimpleDateFormat sdfTarget = new SimpleDateFormat(apiDateFormatOutput, Locale.ENGLISH);

		try {
			Date date = new Date(timestamp);
			return sdfTarget.format(date);
		} catch (Exception e) {
			return "";
		}
	}

	public String parse(AlgoliaHitDto dto) {
		try {
			dto.setDateFormatted(formatDate(dto.getDate()));
			return Crawler.JSON_MAPPER.writeValueAsString(dto);		
		} catch (Exception e) {
			log.error("Error parsing json: {}", e.getMessage());
		}
		return "";
	}
	
	public boolean toFile(AlgoliaHitDto dto, String json) {
		
		boolean saved = false;
		if(enabledToFile) {
			try {
						
				if(!findInFile(dto.getObjectId(), String.valueOf(dto.getDate()))) {
					
					try {
						FileUtils.writeStringToFile(file, json+ "\r\n", StandardCharsets.UTF_8, true);
						saved = true;
					} catch (IOException e) {
						log.error("Error writing to file: {}", e.getMessage());
					}
					
				} else {
					log.debug("Repeated in File! {}", dto.getTitle());
				}
			} catch (Exception e) {
				log.error("Error saving to file: {}", e.getMessage());
			}			
		}
		return saved;
	}
	
	private boolean findInFile(String id, String date) throws Exception {
		
		String currentLine = null;

		try {

			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((currentLine = br.readLine()) != null) {
				if(currentLine.contains(id) && currentLine.contains(date)) {
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}	
	
	public boolean toDatabase(AlgoliaHitDto dto, String json) {
		
		boolean stored = false;
		if(enabledToDatabase) {
			try {
				String databaseId = dto.getObjectId() + "_" + dto.getDate();
				
				Optional<NewsEntity> found = newsRepository.findById(databaseId);						
				if(!found.isPresent()) {
					
					NewsEntity entity = new NewsEntity(
							databaseId,
							dto.getTitle(),
							json);
				
					newsRepository.save(entity);
					stored = true;
					
				} else {
					log.debug("Repeated in DB! {}", databaseId);
				}
				
			} catch (Exception e) {
				log.error("Error saving to db: {}", e.getMessage());
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