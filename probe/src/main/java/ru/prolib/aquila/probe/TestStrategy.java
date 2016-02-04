package ru.prolib.aquila.probe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.CandleSeries;
import ru.prolib.aquila.core.data.timeframe.TFMinutes;

public class TestStrategy implements EventListener, Starter {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TestStrategy.class);
	}
	
	private final Symbol symbol;
	private Terminal terminal;
	private Security security;
	private CandleSeries candles;
	
	public TestStrategy(Terminal terminal, Symbol symbol) {
		super();
		this.terminal = terminal;
		this.symbol = symbol;
	}

	public void onTerminalReady() {
		logger.debug("Terminal Ready.");

		if ( terminal.isSecurityExists(symbol) ) {
			logger.debug("Security exists. Subscribe right now.");
			subscribeOnSecurityEvents();
		} else {
			logger.debug("Security not exists. Wait for availability.");
			terminal.onSecurityAvailable().addListener(this);
		}
		logger.debug("Requesting security: {}", symbol);
		terminal.subscribe(symbol);
	}

	public void onTerminalUnready() {
		try {
			logger.debug("Terminal Unready");
			//if ( candleFiller != null ) {
			//	candleFiller.stop();
			//	logger.debug("Candle filler defined. Stop candle filler.");
			//} else {
			//	logger.debug("Candle filler still undefined. Nothing to do.");
			//}
		} catch ( Exception e ) {
			logger.error("Unexpected exception: {}", e);
		}
	}
	
	private void subscribeOnSecurityEvents() {
		try {
			if ( security == null ) {
				logger.debug("Aquire security instance: {}", symbol);
				security = terminal.getSecurity(symbol);
				//candleFiller = new CandleSeriesFiller(((EditableTerminal) terminal).getEventSystem(), 
				//		security,
				//		new TFMinutes(5), false);
				//candles = candleFiller.getCandles();
				//candles.OnAdded().addListener(this);
			}
			//logger.debug("Start candle filler.");
			//candleFiller.start();
		} catch ( Exception e ) {
			logger.error("Unexpected exception: {}", e);
		}
	}

	@Override
	public void onEvent(Event event) {
		logger.debug("onEvent: {}", event);
		if ( event.isType(terminal.onSecurityAvailable()) ) {
			SecurityEvent e = (SecurityEvent) event;
			if ( e.getSecurity().getSymbol().equals(symbol) ) {
				logger.debug("Wanted security available: {}", symbol);
				terminal.onSecurityAvailable().removeListener(this);
				subscribeOnSecurityEvents();
			}
		} else if ( event.isType(terminal.onTerminalReady()) ) {
			onTerminalReady();
		
		} else if ( event.isType(terminal.onTerminalUnready()) ) {
			onTerminalUnready();
			
		}
	}

	@Override
	public void start() throws StarterException {
		terminal.onTerminalReady().addListener(this);
		terminal.onTerminalUnready().addListener(this);
	}

	@Override
	public void stop() throws StarterException {
		terminal.onTerminalReady().removeListener(this);
		terminal.onTerminalUnready().removeListener(this);
	}

}
