package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.EventType;

public interface MarketDepthStreamContainer extends AbstractContainer {

	public EventType onMarketDepthUpdate();
	
	public MarketDepth getMarketDepth();
	
}
