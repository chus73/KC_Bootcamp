package com.kc.console.rest.business.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kc.console.rest.business.dto.ApiResponseCountDto;
import com.kc.console.rest.model.collections.News;
import com.kc.console.rest.model.repositories.NewsRepository;
import com.querydsl.core.types.Predicate;

@Service
public class NewsService {

	private NewsRepository newsRepository;

	@Autowired
	public NewsService(NewsRepository newsRepository) {
		this.newsRepository = newsRepository;
	}

	public Page<?> find(Predicate predicate, Pageable pageable) {
		return newsRepository.findAll(predicate, pageable);
	}

	public ApiResponseCountDto count(Predicate predicate) {
		
		long count = 0;		
		if(predicate == null) {			
			count = newsRepository.count();
		} else {
			count = newsRepository.count(predicate);
		}
		
		return ApiResponseCountDto.builder().count(count).build();		
	}	
	
	public News findLastNews () {
		return newsRepository.lastNews();
	}

}