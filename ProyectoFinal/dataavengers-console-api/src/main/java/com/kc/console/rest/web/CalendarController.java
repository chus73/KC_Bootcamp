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
import com.kc.console.rest.business.services.CalendarService;
import com.kc.console.rest.model.collections.Calendar;
import com.kc.console.rest.model.repositories.CalendarRepository;
import com.querydsl.core.types.Predicate;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

	@Autowired
	private CalendarService calendarService;

	@GetMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<?> find(@QuerydslPredicate(root = Calendar.class, bindings = CalendarRepository.class) Predicate predicate, Pageable pageable) {					 
		 return calendarService
		     .find(predicate, pageable);	 
    }

	@GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE) 
	public ResponseEntity<ApiResponseCountDto> count(@QuerydslPredicate(root = Calendar.class, bindings = CalendarRepository.class) Predicate predicate) {					 
		ApiResponseCountDto result = calendarService.count(predicate);
		return ResponseEntity.ok(result);
	}
	
	@GetMapping(value = "/reports", produces = MediaType.APPLICATION_JSON_VALUE) 
	public ResponseEntity<List<ChartReport>> reports() {					 
		List<ChartReport> result = calendarService.reports();
		return ResponseEntity.ok(result);
	}

}