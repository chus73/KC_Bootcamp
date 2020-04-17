package com.kc.fxcm.connect.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;

import com.kc.fxcm.connect.ConnectionManager;

@SuppressWarnings("serial")
public class FxcmEventDisconnect extends ApplicationEvent {
	
	Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
	
	public FxcmEventDisconnect(Object source) {
		super(source);		
	} 
}