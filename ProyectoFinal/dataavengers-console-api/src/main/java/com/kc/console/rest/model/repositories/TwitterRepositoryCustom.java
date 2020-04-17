package com.kc.console.rest.model.repositories;

import java.util.List;

import com.kc.console.rest.business.dto.ChartReport;
import com.kc.console.rest.model.collections.Twitter;

public interface TwitterRepositoryCustom {
    
	Twitter maxRetweet();
	
	Twitter maxFavorite();
	
	List<ChartReport> reports();
}