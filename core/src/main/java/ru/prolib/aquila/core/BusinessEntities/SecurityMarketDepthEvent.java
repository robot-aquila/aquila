package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.EventType;

public class SecurityMarketDepthEvent extends SecurityEvent {
	private final MarketDepth marketDepth;
	private final MDUpdate update;

	public SecurityMarketDepthEvent(EventType type, Security security,
			MarketDepth marketDepth, MDUpdate update)
	{
		super(type, security);
		this.marketDepth = marketDepth;
		this.update = update;
	}
	
	public MarketDepth getMarketDepth() {
		return marketDepth;
	}
	
	public MDUpdate getUpdateInfo() {
		return update;
	}

}
