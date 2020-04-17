package com.kc.twitter.tasks;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kc.twitter.api.HttpHelper;
import com.kc.twitter.api.TwitterHelper;
import com.kc.twitter.model.dto.TwitterDto;

@Component
public class TwitterTasks {

	Logger logger = LoggerFactory.getLogger(TwitterTasks.class);
	
	@Autowired
	private HttpHelper httpUtil;

	@Autowired
	private TwitterHelper twitterUtil;
	
	@Value("${api.twitter.url.search.enabled}")
	private boolean searchEnabled;
	
	@Value("${api.twitter.url.timeline.enabled}")
	private boolean timelineEnabled;
	
	@Scheduled(initialDelay = 15000, fixedRateString = "${api.twitter.url.search.scheduled.rate}")
	public void search() {

		if(searchEnabled) {
			searchAction();
		}
	}
	
	private void searchAction() {
		
		logger.info(" --------- start searchAction() ----------- ");
		Map<String, String> paths = twitterUtil.buildSearchPaths();
		
		for(Entry<String, String> entry : paths.entrySet()) {
			
			String result = httpUtil.get(entry.getValue());
			if(result != null) {
				
				List<TwitterDto> dtos = twitterUtil.buildInfoFromSearchResponse(entry.getKey(), result);			
				logger.info("Processed tweets: <{}> for sourceQuery <{}>", dtos.size(), entry.getKey());
									
				for(TwitterDto dto : dtos) {						
					String json = twitterUtil.parse(dto);					
					boolean stored = twitterUtil.toDatabase(dto, json);					
					boolean saved  = twitterUtil.toFile(dto, json);		
					
					boolean sent = false;
					if(twitterUtil.shouldSendToKafka(stored, saved)) {
						sent  = twitterUtil.toKafka(json);
					}
					
					logger.debug("Stored in DB: {}", stored);
					logger.debug("Saved in file: {}", saved);
					logger.debug("Sent to Kafka: {}", sent);				
				}		
			}			
		}
		logger.info(" --------- end searchAction() ----------- ");
	}
	
	@Scheduled(initialDelay = 30000, fixedRateString = "${api.twitter.url.timeline.scheduled.rate}")
	public void timeline() {
		
		if(timelineEnabled) {
			timelineAction();
		}
	}
	
	private void timelineAction() {
		
		logger.info(" --------- start timelineAction() ----------- ");
		Map<String, String> paths = twitterUtil.buildTimelinePaths();
		
		for(Entry<String, String> entry : paths.entrySet()) {
			
			String result = httpUtil.get(entry.getValue());
			if(result != null) {
				
				List<TwitterDto> dtos = twitterUtil.buildInfoFromTimelineResponse(entry.getKey(), result);			
				logger.info("Processed tweets: <{}> for sourceQuery <{}>", dtos.size(), entry.getKey());
									
				for(TwitterDto dto : dtos) {						
					String json = twitterUtil.parse(dto);					
					boolean stored = twitterUtil.toDatabase(dto, json);					
					boolean saved  = twitterUtil.toFile(dto, json);		
					
					boolean sent = false;
					if(twitterUtil.shouldSendToKafka(stored, saved)) {
						sent  = twitterUtil.toKafka(json);
					}
					
					logger.debug("Stored in DB: {}", stored);
					logger.debug("Saved in file: {}", saved);
					logger.debug("Sent to Kafka: {}", sent);				
				}		
			}			
		}
		logger.info(" --------- end timelineAction() ----------- ");
	}
		
}
