package com.kc.fxcm.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kc.fxcm.model.entities.FxcmEntity;

public interface FxcmRepository extends JpaRepository<FxcmEntity, String> {

}
