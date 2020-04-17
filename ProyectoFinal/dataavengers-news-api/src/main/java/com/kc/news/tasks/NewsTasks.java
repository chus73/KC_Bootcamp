package com.kc.news.tasks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.kc.news.crawler.AlgoliaHitDto;
import com.kc.news.crawler.AlgoliaResultDto;
import com.kc.news.crawler.Crawler;
import com.kc.news.crawler.NewsHelper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NewsTasks {

	@Autowired
	private Crawler crawler;
	
	@Autowired
	private NewsHelper newsHelper;

	@Value("${api.news.url}")
	private String apiUrl;

	@Value("${api.news.page-items}")
	private Integer apiPageItems;

	@Value("${api.news.page-application-id}")
	private String apiAppId;

	@Value("${api.news.page-api-key}")
	private String apiKey;

	@Scheduled(initialDelay = 15000, fixedRateString = "${api.news.scheduled.rate}")
	public void search() {

		//List<AlgoliaHitDto> hitResults = new ArrayList<AlgoliaHitDto>();
		//hitResults = crawlAction();
		//sendAction(hitResults);	
		crawlAction();
	}
	
	private void crawlAction() {
				
		log.info(" --------- start crawlAction() ----------- ");
		//List<AlgoliaHitDto> hitResults = new ArrayList<AlgoliaHitDto>();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(apiUrl)
				.queryParam("x-algolia-application-id", apiAppId)
				.queryParam("x-algolia-api-key", apiKey);

		String inputBase = "{\"requests\":[{\"indexName\":\"FxsIndexPro\",\"params\":\"query=&hitsPerPage={items-each-page}&maxValuesPerFacet=9999&page={items-page}&filters=CultureName%3Aen%20AND%20(Category%3A'News')&facets=%5B%22Tags%22%2C%22AuthorName%22%5D&tagFilters=\"}]}";

		try {
			
			AlgoliaResultDto firstResult = crawler.fillAlgoliaResult(inputBase, 0, apiPageItems, headers, urlBuilder.toUriString());	
			
			log.info("Page 0, news found: {}", firstResult.getHits().size());
			//hitResults.addAll(firstResult.getHits());
			sendAction(firstResult.getHits(), 0);			
			
			log.info("Total pages: {}", firstResult.getNbPages());
			for (int page = 1; page < firstResult.getNbPages(); page++) {
				AlgoliaResultDto pageResult = crawler.fillAlgoliaResult(inputBase, page, apiPageItems, headers, urlBuilder.toUriString());
				log.info("Page {}, news found: {}", page, pageResult.getHits().size());
				//hitResults.addAll(pageResult.getHits());
				sendAction(pageResult.getHits(), page);		
			}
			
		} catch (Exception e) {
			log.error("Error getting : {}", e.getMessage());
		}
		
		log.info(" --------- end crawlAction() ----------- ");
		//return hitResults;		
	}
	
	private void sendAction(List<AlgoliaHitDto> hitResults, int page) {
		
		log.info(" --------- start sendAction(). Page {} ----------- ", page);
		
		try {
			
			for(AlgoliaHitDto hit : hitResults) {
				
				String json = newsHelper.parse(hit);					
				boolean stored = newsHelper.toDatabase(hit, json);					
				boolean saved  = newsHelper.toFile(hit, json);		
				
				boolean sent = false;
				if(newsHelper.shouldSendToKafka(stored, saved)) {
					sent  = newsHelper.toKafka(json);
				}
				
				log.debug("Stored in DB: {}", stored);
				log.debug("Saved in file: {}", saved);
				log.debug("Sent to Kafka: {}", sent);					
			}	
			
		} catch (Exception e) {
			log.error("Error sending : {}", e.getMessage());
		}
		
		log.info(" --------- end sendAction() ----------- ");
	}

}
