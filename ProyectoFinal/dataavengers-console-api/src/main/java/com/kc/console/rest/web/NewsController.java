package com.kc.console.rest.web;
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
import com.kc.console.rest.business.services.NewsService;
import com.kc.console.rest.model.collections.News;
import com.kc.console.rest.model.repositories.NewsRepository;
import com.querydsl.core.types.Predicate;

@RestController
@RequestMapping("/api/news")
public class NewsController {

	@Autowired
	private NewsService newsService;

	@GetMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<?> find(@QuerydslPredicate(root = News.class, bindings = NewsRepository.class) Predicate predicate, Pageable pageable) {					 
		 return newsService
		     .find(predicate, pageable);	 
    }

	@GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE) 
	public ResponseEntity<ApiResponseCountDto> count(@QuerydslPredicate(root = News.class, bindings = NewsRepository.class) Predicate predicate) {					 
		ApiResponseCountDto result = newsService.count(predicate);
		return ResponseEntity.ok(result);
	}
	
	@GetMapping(value = "/last", produces = MediaType.APPLICATION_JSON_VALUE) 
	public ResponseEntity<News> maxFavorite() {					 
		News result = newsService.findLastNews();
		return ResponseEntity.ok(result);
	}

}