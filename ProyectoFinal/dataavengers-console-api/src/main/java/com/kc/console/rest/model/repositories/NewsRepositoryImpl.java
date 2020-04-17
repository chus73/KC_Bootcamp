package com.kc.console.rest.model.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.kc.console.rest.model.collections.News;

@Component
public class NewsRepositoryImpl implements NewsRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public News lastNews() {
		final Query query = new Query()
                .limit(1)
                .with(new Sort(Sort.Direction.DESC, "PublicationTime"));
        return mongoTemplate.findOne(query, News.class);
	}


}
