package com.kc.fxcm.connect.events;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class FxcmEventCreate extends ApplicationEvent {

	public FxcmEventCreate(Object source) {
		super(source);
	}    	
}