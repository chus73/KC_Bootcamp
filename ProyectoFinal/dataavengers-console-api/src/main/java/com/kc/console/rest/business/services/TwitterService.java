package com.kc.console.rest.business.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kc.console.rest.business.dto.ApiResponseCountDto;
import com.kc.console.rest.business.dto.ChartReport;
import com.kc.console.rest.model.collections.Twitter;
import com.kc.console.rest.model.repositories.TwitterRepository;
import com.querydsl.core.types.Predicate;

@Service
public class TwitterService {

	private TwitterRepository twitterRepository;

	@Autowired
	public TwitterService(TwitterRepository twitterRepository) {
		this.twitterRepository = twitterRepository;
	}

	public Page<?> find(Predicate predicate, Pageable pageable) {
		return twitterRepository.findAll(predicate, pageable);
	}

	public ApiResponseCountDto count(Predicate predicate) {
		
		long count = 0;		
		if(predicate == null) {			
			count = twitterRepository.count();
		} else {
			count = twitterRepository.count(predicate);
		}

		return ApiResponseCountDto.builder().count(count).build();		
	}	
	
	public Twitter findMaxRetweet() {
		return twitterRepository.maxRetweet();
	}
	
	public Twitter findMaxFavorite() {
		return twitterRepository.maxFavorite();
	}
	
	public List<ChartReport> reports() {
		return twitterRepository.reports();
	}

}