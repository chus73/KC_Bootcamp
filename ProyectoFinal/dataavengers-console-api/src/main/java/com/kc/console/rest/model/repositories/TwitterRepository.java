package com.kc.console.rest.model.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import com.kc.console.rest.model.collections.QTwitter;
import com.kc.console.rest.model.collections.Twitter;
import com.querydsl.core.types.dsl.StringPath;

@Repository
public interface TwitterRepository extends 
		MongoRepository<Twitter, ObjectId>,
		QuerydslPredicateExecutor<Twitter>,
		QuerydslBinderCustomizer<QTwitter>, 
		TwitterRepositoryCustom {

	@Override
	default public void customize(QuerydslBindings bindings, QTwitter root) {
		bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
	}

}