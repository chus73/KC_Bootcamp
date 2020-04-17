package com.kc.console.rest.model.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import com.kc.console.rest.model.collections.Fxcm;
import com.kc.console.rest.model.collections.QFxcm;
import com.querydsl.core.types.dsl.StringPath;

@Repository
public interface FxcmRepository extends 
		MongoRepository<Fxcm, ObjectId>,
		QuerydslPredicateExecutor<Fxcm>,
		QuerydslBinderCustomizer<QFxcm> {

	@Override
	default public void customize(QuerydslBindings bindings, QFxcm root) {
		bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
	}
}