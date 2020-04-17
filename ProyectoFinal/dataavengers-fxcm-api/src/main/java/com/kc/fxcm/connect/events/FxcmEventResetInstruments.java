package com.kc.fxcm.connect.events;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class FxcmEventResetInstruments extends ApplicationEvent {

	public FxcmEventResetInstruments(Object source) {
		super(source);
	}    	
}