package com.kc.fxcm.connect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.kc.fxcm.connect.events.FxcmEventConnect;
import com.kc.fxcm.connect.events.FxcmEventConnectError;
import com.kc.fxcm.connect.events.FxcmEventConnectTimeout;
import com.kc.fxcm.connect.events.FxcmEventConnecting;
import com.kc.fxcm.connect.events.FxcmEventCreate;
import com.kc.fxcm.connect.events.FxcmEventDisconnect;
import com.kc.fxcm.connect.events.FxcmEventError;
import com.kc.fxcm.connect.events.FxcmEventReconnect;
import com.kc.fxcm.connect.events.FxcmEventUpdateInstruments;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;

@Component
public class ConnectionManager {

	Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
	
	public static String AUTH_HTTP_HEADER = "Bearer";

	@Value("${api.url}")
	private String apiUrl;

	@Value("${api.login.accountId}")
	private String apiLoginAccountId;

	@Value("${api.login.token}")
	private String apiLoginToken;
	
	@Autowired
    private ApplicationEventPublisher eventPublisher;	
	
	@Autowired
	private FxcmApiRepository tokenRepository;

	private Socket serverSocket;
		
	@EventListener
	public void fxcmEventCreateHandler(FxcmEventCreate event) {

		logger.info("Handling FxcmEventCreate to {}", apiUrl);

		tokenRepository.setConnected(false);
		IO.Options options = new IO.Options();
		options.forceNew = true;
		final OkHttpClient client = new OkHttpClient();
		options.webSocketFactory = client;
		options.callFactory = client;
		options.query = "access_token=" + apiLoginToken;
		
		try {
			serverSocket = IO.socket(apiUrl, options);

			serverSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					eventPublisher.publishEvent(new FxcmEventConnect(this, serverSocket.id()));
				}
			});
			
			serverSocket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					eventPublisher.publishEvent(new FxcmEventConnectTimeout(this));
				}
			});
			
			serverSocket.on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					eventPublisher.publishEvent(new FxcmEventConnecting(this));
				}
			});
			
			serverSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					eventPublisher.publishEvent(new FxcmEventConnectError(this));
				}
			});
			
			serverSocket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					eventPublisher.publishEvent(new FxcmEventError(this, serverSocket.id()));
				}
			});
			
			serverSocket.on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					eventPublisher.publishEvent(new FxcmEventReconnect(this, serverSocket.id()));
				}
			});
						
			serverSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					eventPublisher.publishEvent(new FxcmEventDisconnect(this));
				}
			});

			serverSocket.on(io.socket.engineio.client.Socket.EVENT_CLOSE, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					logger.info("engine close");
					client.dispatcher().executorService().shutdown();
					eventPublisher.publishEvent(new FxcmEventCreate(this));
				}
			});

			serverSocket.open();

		} catch (Exception e) {
			logger.info("FxcmEventCreate error: {}", e.getMessage());
			eventPublisher.publishEvent(new FxcmEventCreate(this));
		}
	}
	
	@EventListener
	public void fxcmEventConnectHandler(FxcmEventConnect event) {			
		tokenRepository.setBearerToken(ConnectionManager.AUTH_HTTP_HEADER + " " + event.getServerSocketId() + apiLoginToken);	
		tokenRepository.setBearerTokenValue(event.getServerSocketId() + apiLoginToken);
		
		logger.info("FxcmEventConnect, Server Socket ID: " + event.getServerSocketId());
		logger.info("FxcmEventConnect, bearer token: " + tokenRepository.getBearerToken());	
		
		tokenRepository.setConnected(true);
		
		eventPublisher.publishEvent(new FxcmEventUpdateInstruments(this));
	}
	
	@EventListener
	public void fxcmEventConnectingHandler(FxcmEventConnecting event) {			
		logger.info("FxcmEventConnecting");
		tokenRepository.setConnected(false);
	}
	
	@EventListener
	public void fxcmEventConnectErrorHandler(FxcmEventConnectError event) {			
		logger.info("FxcmEventConnectError");
		tokenRepository.setConnected(false);
	}
	
	@EventListener
	public void fxcmEventConnectTimeoutHandler(FxcmEventConnectTimeout event) {			
		logger.info("FxcmEventConnectTimeout");
		tokenRepository.setConnected(false);
	}
	
	@EventListener
	public void fxcmEventReconnectHandler(FxcmEventReconnect event) {			
		logger.info("FxcmEventReconnect, Server Socket ID: " + event.getServerSocketId());
		tokenRepository.setConnected(false);
	}
	
	@EventListener
	public void fxcmEventDisconnectHandler(FxcmEventDisconnect event) {			
		logger.info("FxcmEventDisconnect");
		tokenRepository.setConnected(false);
	}
}
