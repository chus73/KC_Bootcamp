package com.kc.fxcm.api;

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

	@Value("${api.host}")
	private String apiHost;

	@Value("${api.port}")
	private String apiPort;

	@Value("${api.url}")
	private String apiUrl;

	@Autowired
	private RestTemplate restClient;
	
	public String post(String path, String token, String body) {
		
		final String requestPath = apiUrl + path;
		logger.debug("REST POST starting <{}> : <{}>", requestPath, body);
		
		try {
			
			RequestEntity<String> request = RequestEntity
					.post(new URI(requestPath))				
					.header(HttpHeaders.HOST, apiHost)
					.header("port", apiPort)
					.header("path", path)
					.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
					.header(HttpHeaders.USER_AGENT, "request")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
					.header(HttpHeaders.AUTHORIZATION, token)
					.body(body);
			
			ResponseEntity<String> response = restClient.exchange(request, String.class);			
			logger.debug("REST POST executed <{}>", response);		
			
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
	
	public String get(String path, String token) {
		
		final String requestPath = apiUrl + path;
		logger.debug("REST GET starting <{}>", requestPath);

		try {

			RequestEntity<Void> request = RequestEntity
				.get(new URI(requestPath))
				.header(HttpHeaders.HOST, apiHost)
				.header("port", apiPort)
				.header("path", path)
				.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.header(HttpHeaders.USER_AGENT, "request")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.header(HttpHeaders.AUTHORIZATION, token)
				.build();
			
			ResponseEntity<String> response = restClient.exchange(request, String.class);			
			logger.debug("REST GET executed <{}>", response);		
			
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
