package com.kc.calendar.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kc.calendar.model.entities.CalendarEntity;

public interface CalendarRepository extends JpaRepository<CalendarEntity, String> {

}
