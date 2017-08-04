package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

import ru.prolib.aquila.core.EventType;

/**
 * Security's Depth of Market update event.
 */
public class SecurityMarketDepthEvent extends SecurityEvent {
	private final MarketDepth marketDepth;

	/**
	 * Constructor.
	 * <p>
	 * @param type - event type
	 * @param security - security instance
	 * @param time - time of event
	 * @param marketDepth - market depth
	 */
	public SecurityMarketDepthEvent(EventType type, Security security, Instant time, MarketDepth marketDepth) {
		super(type, security, time);
		this.marketDepth = marketDepth;
	}
	
	public MarketDepth getMarketDepth() {
		return marketDepth;
	}

}
