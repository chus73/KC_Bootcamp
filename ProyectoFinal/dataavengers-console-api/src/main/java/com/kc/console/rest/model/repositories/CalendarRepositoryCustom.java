package com.kc.console.rest.model.repositories;

import java.util.List;

import com.kc.console.rest.business.dto.ChartReport;

public interface CalendarRepositoryCustom {
    
	List<ChartReport> reports();
}