package com.kc.fxcm.connect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;

import com.kc.fxcm.model.dto.InstrumentDto;

@Component
public class FxcmApiRepository {
	
	private List<InstrumentDto> instrumentsCache = Collections.synchronizedList(new ArrayList<InstrumentDto>());
		
	private AtomicBoolean connected = new AtomicBoolean(false);
	private AtomicReference<String> bearerToken = new AtomicReference<String>();
	private AtomicReference<String> bearerTokenValue = new AtomicReference<String>();
	
	public String getBearerToken() {
		return bearerToken.get();
	}
	
	public String getBearerTokenValue() {
		return bearerTokenValue.get();
	}
	
	public void setBearerToken(String bearerToken) {
		this.bearerToken.set(bearerToken);
	}
	
	public void setBearerTokenValue(String bearerTokenValue) {
		this.bearerTokenValue.set(bearerTokenValue);
	}
	
	public boolean isConnected() {
		return this.connected.get();
	}
	
	public void setConnected(boolean connected) {
		this.connected.set(connected);
	}
	
	public List<InstrumentDto> getInstrumentsCache() {
		return instrumentsCache;
	}
	
	public void setInstrumentsCache(List<InstrumentDto> instrumentsCache) {
		this.instrumentsCache = instrumentsCache;
	}

}
