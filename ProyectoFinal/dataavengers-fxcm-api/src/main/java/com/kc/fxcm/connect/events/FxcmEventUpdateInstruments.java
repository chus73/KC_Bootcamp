package com.kc.fxcm.connect.events;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class FxcmEventUpdateInstruments extends ApplicationEvent {

	public FxcmEventUpdateInstruments(Object source) {
		super(source);
	}    	
}