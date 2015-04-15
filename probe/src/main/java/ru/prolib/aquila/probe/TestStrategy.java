package ru.prolib.aquila.probe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.CandleSeries;
import ru.prolib.aquila.core.data.filler.CandleSeriesFiller;
import ru.prolib.aquila.core.data.timeframe.TFMinutes;

public class TestStrategy implements EventListener, Starter {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TestStrategy.class);
	}
	
	private final SecurityDescriptor descr;
	private Terminal terminal;
	private Security security;
	private CandleSeriesFiller candleFiller;
	private CandleSeries candles;
	
	public TestStrategy(Terminal terminal, SecurityDescriptor descr) {
		super();
		this.terminal = terminal;
		this.descr = descr;
	}

	public void onTerminalReady() {
		logger.debug("Terminal Ready.");

		if ( terminal.isSecurityExists(descr) ) {
			logger.debug("Security exists. Subscribe right now.");
			subscribeOnSecurityEvents();
		} else {
			logger.debug("Security not exists. Wait for availability.");
			terminal.OnSecurityAvailable().addListener(this);
		}
		logger.debug("Requesting security: {}", descr);
		terminal.requestSecurity(descr);
	}

	public void onTerminalUnready() {
		try {
			logger.debug("Terminal Unready");
			if ( candleFiller != null ) {
				candleFiller.stop();
				logger.debug("Candle filler defined. Stop candle filler.");
			} else {
				logger.debug("Candle filler still undefined. Nothing to do.");
			}
		} catch ( Exception e ) {
			logger.error("Unexpected exception: {}", e);
		}
	}
	
	private void subscribeOnSecurityEvents() {
		try {
			if ( security == null ) {
				logger.debug("Aquire security instance: {}", descr);
				security = terminal.getSecurity(descr);
				candleFiller = new CandleSeriesFiller(((EditableTerminal) terminal).getEventSystem(), 
						security,
						new TFMinutes(5), false);
				candles = candleFiller.getCandles();
				candles.OnAdded().addListener(this);
			}
			logger.debug("Start candle filler.");
			candleFiller.start();
		} catch ( Exception e ) {
			logger.error("Unexpected exception: {}", e);
		}
	}

	@Override
	public void onEvent(Event event) {
		logger.debug("onEvent: {}", event);
		if ( event.isType(terminal.OnSecurityAvailable()) ) {
			SecurityEvent e = (SecurityEvent) event;
			if ( e.getSecurity().getDescriptor().equals(descr) ) {
				logger.debug("Wanted security available: {}", descr);
				terminal.OnSecurityAvailable().removeListener(this);
				subscribeOnSecurityEvents();
			}
		} else if ( event.isType(terminal.OnReady()) ) {
			onTerminalReady();
		
		} else if ( event.isType(terminal.OnUnready()) ) {
			onTerminalUnready();
			
		} else if ( candles != null && event.isType(candles.OnAdded()) ) {
			logger.debug("Candle added.");
		}
	}

	@Override
	public void start() throws StarterException {
		terminal.OnReady().addListener(this);
		terminal.OnUnready().addListener(this);
	}

	@Override
	public void stop() throws StarterException {
		terminal.OnReady().removeListener(this);
		terminal.OnUnready().removeListener(this);
	}

}
