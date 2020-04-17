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
import com.kc.console.rest.business.services.FxcmService;
import com.kc.console.rest.model.collections.Fxcm;
import com.kc.console.rest.model.repositories.FxcmRepository;
import com.querydsl.core.types.Predicate;

@RestController
@RequestMapping("/api/fxcm")
public class FxcmController {

	@Autowired
	private FxcmService fxcmService;

	@GetMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<?> find(@QuerydslPredicate(root = Fxcm.class, bindings = FxcmRepository.class) Predicate predicate, Pageable pageable) {					 
		 return fxcmService
		     .find(predicate, pageable);	 
    }

	@GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE) 
	public ResponseEntity<ApiResponseCountDto> count(@QuerydslPredicate(root = Fxcm.class, bindings = FxcmRepository.class) Predicate predicate) {					 
		ApiResponseCountDto result = fxcmService.count(predicate);
		return ResponseEntity.ok(result);
	}

}