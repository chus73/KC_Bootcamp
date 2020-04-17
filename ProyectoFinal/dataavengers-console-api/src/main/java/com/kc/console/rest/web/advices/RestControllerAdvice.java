package com.kc.console.rest.web.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.kc.console.rest.business.dto.ApiResponseErrorDto;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@RestController
@Slf4j
public class RestControllerAdvice extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ApiResponseErrorDto> handleAllExceptions(Exception ex, WebRequest request) {
		
		log.error("General error {}" , ex.getMessage());
		ApiResponseErrorDto error = ApiResponseErrorDto.builder()
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.exceptionType(ex.getClass().getSimpleName())
				.exceptionMessage(ex.getMessage())
				.description(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
				.build();
		
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
