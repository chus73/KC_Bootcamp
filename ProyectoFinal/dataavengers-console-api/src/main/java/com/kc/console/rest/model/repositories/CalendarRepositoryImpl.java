package com.kc.console.rest.model.repositories;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Component;

import com.kc.console.rest.business.dto.ChartReport;
import com.kc.console.rest.model.collections.Calendar;

@Component
public class CalendarRepositoryImpl implements CalendarRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<ChartReport> reports() {
		
		Aggregation aggregation = newAggregation(
				group("importance").count().as("total"));
		
		AggregationResults<ChartReport> reports = mongoTemplate.aggregate(
				aggregation, Calendar.class, ChartReport.class); 
		
		List<ChartReport> salesReport = reports.getMappedResults();
		
		return salesReport
				.stream()
				.filter(report -> {
				return (report.total > 100);
        }).collect(Collectors.toList());
	}

}
