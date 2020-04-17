package com.kc.console.rest.business.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kc.console.rest.business.dto.ApiResponseCountDto;
import com.kc.console.rest.model.repositories.FxcmRepository;
import com.querydsl.core.types.Predicate;

@Service
public class FxcmService {

	private FxcmRepository fxcmRepository;

	@Autowired
	public FxcmService(FxcmRepository calendarRepository) {
		this.fxcmRepository = calendarRepository;
	}

	public Page<?> find(Predicate predicate, Pageable pageable) {
		return fxcmRepository.findAll(predicate, pageable);
	}

	public ApiResponseCountDto count(Predicate predicate) {
		
		long count = 0;		
		if(predicate == null) {			
			count = fxcmRepository.count();
		} else {
			count = fxcmRepository.count(predicate);
		}
		
		return ApiResponseCountDto.builder().count(count).build();		
	}	

}