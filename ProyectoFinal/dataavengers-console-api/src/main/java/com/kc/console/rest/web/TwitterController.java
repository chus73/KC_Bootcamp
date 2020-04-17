package com.kc.console.rest.web;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kc.console.rest.business.dto.ApiResponseCountDto;
import com.kc.console.rest.business.dto.ChartReport;
import com.kc.console.rest.business.services.TwitterService;
import com.kc.console.rest.model.collections.Twitter;
import com.kc.console.rest.model.repositories.TwitterRepository;
import com.querydsl.core.types.Predicate;

@RestController
@RequestMapping("/api/twitter")
public class TwitterController {

	@Autowired
	private TwitterService twitterService;

	@GetMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<?> find(@QuerydslPredicate(root = Twitter.class, bindings = TwitterRepository.class) Predicate predicate, Pageable pageable) {					 
		 return twitterService
		     .find(predicate, pageable);	 
    }

	@GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE) 
	public ResponseEntity<ApiResponseCountDto> count(@QuerydslPredicate(root = Twitter.class, bindings = TwitterRepository.class) Predicate predicate) {					 
		ApiResponseCountDto result = twitterService.count(predicate);
		return ResponseEntity.ok(result);
	}
	
	@GetMapping(value = "/max/retweet", produces = MediaType.APPLICATION_JSON_VALUE) 
	public ResponseEntity<Twitter> maxRetweet() {					 
		Twitter result = twitterService.findMaxRetweet();
		return ResponseEntity.ok(result);
	}
	
	@GetMapping(value = "/max/favorite", produces = MediaType.APPLICATION_JSON_VALUE) 
	public ResponseEntity<Twitter> maxFavorite() {					 
		Twitter result = twitterService.findMaxFavorite();
		return ResponseEntity.ok(result);
	}
	
	@GetMapping(value = "/reports", produces = MediaType.APPLICATION_JSON_VALUE) 
	public ResponseEntity<List<ChartReport>> reports() {					 
		List<ChartReport> result = twitterService.reports();
		return ResponseEntity.ok(result);
	}

}