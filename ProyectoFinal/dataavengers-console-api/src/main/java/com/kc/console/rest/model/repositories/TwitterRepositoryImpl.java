package com.kc.console.rest.model.repositories;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.kc.console.rest.business.dto.ChartReport;
import com.kc.console.rest.model.collections.Twitter;

@Component
public class TwitterRepositoryImpl implements TwitterRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public Twitter maxRetweet() {
		final Query query = new Query()
                .limit(1)
                .with(new Sort(Sort.Direction.DESC, "retweet_count"));
        return mongoTemplate.findOne(query, Twitter.class);
	}

	@Override
	public Twitter maxFavorite() {
		final Query query = new Query()
                .limit(1)
                .with(new Sort(Sort.Direction.DESC, "favorite_count"));
        return mongoTemplate.findOne(query, Twitter.class);
	}

	@Override
	public List<ChartReport> reports() {
		
		Aggregation aggregation = newAggregation(
				group("sourceQuery").count().as("total"));
		
		AggregationResults<ChartReport> reports = mongoTemplate.aggregate(
				aggregation, Twitter.class, ChartReport.class);
 
		
		List<ChartReport> salesReport = reports.getMappedResults();
		 
		return salesReport;
	}
	
	/*
	  _id: "$sourceQuery",
	  total: {
	    $sum: 1
	  }
	 */

}
