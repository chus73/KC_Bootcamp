package com.kc.twitter.api;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpHelper {

	Logger logger = LoggerFactory.getLogger(HttpHelper.class);

	@Value("${api.twitter.url}")
	private String apiUrl;
	
	@Value("${api.twitter.token}")
	private String apiBearer;

	@Autowired
	private RestTemplate restClient;
	
	public String get(String path) {
		
		final String requestPath = apiUrl + path;
		final String token = "Bearer " + apiBearer;
		
		logger.info("REST to twitter {}", requestPath);

		try {

			RequestEntity<Void> request = RequestEntity
				.get(new URI(requestPath))
				.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header(HttpHeaders.AUTHORIZATION, token)
				.build();
			
			ResponseEntity<String> response = restClient.exchange(request, String.class);			
			logger.info("REST returned <{}>", response.getStatusCodeValue());		
			
			if(response.getStatusCode() == HttpStatus.OK) {
				return response.getBody();
			} else {
				throw new Exception("HttpError: " + response.getStatusCodeValue());
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}	
}
