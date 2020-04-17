package com.kc.console.rest.business.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kc.console.rest.business.dto.ApiResponseCountDto;
import com.kc.console.rest.business.dto.ChartReport;
import com.kc.console.rest.model.repositories.CalendarRepository;
import com.querydsl.core.types.Predicate;

@Service
public class CalendarService {

	private CalendarRepository calendarRepository;

	@Autowired
	public CalendarService(CalendarRepository calendarRepository) {
		this.calendarRepository = calendarRepository;
	}

	public Page<?> find(Predicate predicate, Pageable pageable) {
		return calendarRepository.findAll(predicate, pageable);
	}

	public ApiResponseCountDto count(Predicate predicate) {
		
		long count = 0;		
		if(predicate == null) {			
			count = calendarRepository.count();
		} else {
			count = calendarRepository.count(predicate);
		}
		
		return ApiResponseCountDto.builder().count(count).build();		
	}	
	
	public List<ChartReport> reports() {
		return calendarRepository.reports();
	}

}