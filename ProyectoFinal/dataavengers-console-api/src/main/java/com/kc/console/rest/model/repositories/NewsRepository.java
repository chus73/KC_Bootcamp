package com.kc.console.rest.model.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import com.kc.console.rest.model.collections.News;
import com.kc.console.rest.model.collections.QNews;
import com.querydsl.core.types.dsl.StringPath;

@Repository
public interface NewsRepository extends 
		MongoRepository<News, ObjectId>,
		QuerydslPredicateExecutor<News>,
		QuerydslBinderCustomizer<QNews>,
		NewsRepositoryCustom {

	@Override
	default public void customize(QuerydslBindings bindings, QNews root) {
		bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
	}
}