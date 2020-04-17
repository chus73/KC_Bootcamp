package com.kc.fxcm.connect.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;

import com.kc.fxcm.connect.ConnectionManager;

@SuppressWarnings("serial")
public class FxcmEventConnect extends ApplicationEvent {
	
	Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
	
	private String serverSocketId;

	public FxcmEventConnect(Object source, String serverSocketId) {
		super(source);
		this.serverSocketId = serverSocketId;					
	} 
	
	public String getServerSocketId() {
		return serverSocketId;
	}
}