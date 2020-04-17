package com.kc.twitter.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kc.twitter.model.entities.TwitterEntity;

public interface TwitterRepository extends JpaRepository<TwitterEntity, Long> {

}
