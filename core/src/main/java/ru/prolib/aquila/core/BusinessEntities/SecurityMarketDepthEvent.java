package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.EventType;

public class SecurityMarketDepthEvent extends SecurityEvent {
	private final MarketDepth marketDepth;

	public SecurityMarketDepthEvent(EventType type, Security security, MarketDepth marketDepth) {
		super(type, security);
		this.marketDepth = marketDepth;
	}
	
	public MarketDepth getMarketDepth() {
		return marketDepth;
	}

}
