package com.kc.news.crawler;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Crawler {

	public static final ObjectMapper JSON_MAPPER = new ObjectMapper()
			.registerModule(new JsonOrgModule())
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@Autowired
	private RestTemplate restTemplate;
	
	public AlgoliaResultDto fillAlgoliaResult(String inputBase, int page, Integer itemsEachPage, HttpHeaders headers,
			String url) {

		String currentInput = inputBase
				.replace("{items-each-page}", String.valueOf(itemsEachPage))
				.replace("{items-page}", String.valueOf(page));
		HttpEntity<String> request = new HttpEntity<String>(currentInput, headers);
		
		log.info("Getting info for page <{}>", page);
		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
		AlgoliaResultDto algoliaResult = parseAlgoliaResult(response.getBody());

		if (algoliaResult != null) {

			log.debug("Retrieved hits: {}", algoliaResult.getHits().size());
			for (AlgoliaHitDto hit : algoliaResult.getHits()) {

				if (!StringUtils.isEmpty(hit.getFullUrl())) {

					for (int retries = 1; retries <= 3; retries++) {

						try {

							log.debug("Getting article from  Retry:{} : {}", retries, hit.getFullUrl());
							Document articleDoc = Jsoup.connect(hit.getFullUrl()).userAgent("Mozilla").get();
							Elements content = articleDoc.select("div.fxs_article_content");
							String contentString = content.text();
							if (!StringUtils.isEmpty(contentString)) {
								hit.setArticle(contentString);
							} else {
								hit.setArticle("");
							}

							retries = 3;
							break;

						} catch (Exception e) {
							log.error("Error getting content for Retry:{}, Hit:{}", retries, hit.getFullUrl());
							hit.setArticle("");
						}
					}

				} else {
					log.error("Error getFullUrl is empty or null {}", hit.getFullUrl());
					hit.setArticle("");
				}
			}
		}
		
		return algoliaResult;
	}
	
	private AlgoliaResultDto parseAlgoliaResult(String responseBody) {

		try {

			JSONObject responseJson = new JSONObject(responseBody);
			JSONArray results = responseJson.getJSONArray("results");
			JSONObject hitsJson = results.getJSONObject(0);
			log.debug("hitsJson: {}", hitsJson);
			AlgoliaResultDto parsedResult = JSON_MAPPER.convertValue(hitsJson, AlgoliaResultDto.class);
			return parsedResult;

		} catch (Exception e) {
			log.error("Error parsing result : {}", e.getMessage());
			return null;
		}

	}
}