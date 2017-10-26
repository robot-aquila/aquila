package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityTickEvent;

public class CSLastTradeFiller implements CSFiller, EventListener {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(CSLastTradeFiller.class);
	}
	
	private final CSUtils utils;
	private final Security security;
	private final ZTFrame tf;
	private final ObservableSeriesImpl<Candle> series;
	private boolean started;
	
	public CSLastTradeFiller(Security security, ZTFrame tf,
			ObservableSeriesImpl<Candle> series, CSUtils utils)
	{
		this.utils = utils;
		this.security = security;
		this.tf = tf;
		this.series = series;
	}
	
	public Security getSecurity() {
		return security;
	}
	
	public CSUtils getUtils() {
		return utils;
	}

	@Override
	public ObservableSeries<Candle> getSeries() {
		return series;
	}

	@Override
	public ZTFrame getTF() {
		return tf;
	}

	@Override
	public synchronized void start() {
		if ( ! started ) {
			security.onLastTrade().addListener(this);
			started = true;
		}
	}

	@Override
	public synchronized void stop() {
		if ( started ) {
			security.onLastTrade().removeListener(this);
			started = false;
		}
	}

	@Override
	public synchronized boolean isStarted() {
		return started;
	}

	@Override
	public void onEvent(Event event) {
		if ( ! isStarted() ) {
			return;
		}
		SecurityTickEvent e = (SecurityTickEvent) event;
		try {
			utils.aggregate(series, tf, e.getTick());
		} catch ( ValueException exception ) {
			logger.error("Unexpected exception: ", e);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( this == other ) {
			return true;
		}
		if ( other == null || other.getClass() != CSLastTradeFiller.class ) {
			return false;
		}
		CSLastTradeFiller o = (CSLastTradeFiller) other;
		return new EqualsBuilder()
				.append(started, o.started)
				.append(utils, o.utils)
				.append(security, o.security)
				.append(series, o.series)
				.append(tf, o.tf)
				.isEquals();
	}
	
}