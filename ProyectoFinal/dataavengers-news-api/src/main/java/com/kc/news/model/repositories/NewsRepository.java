package com.kc.news.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kc.news.model.entities.NewsEntity;

public interface NewsRepository extends JpaRepository<NewsEntity, String> {

}
