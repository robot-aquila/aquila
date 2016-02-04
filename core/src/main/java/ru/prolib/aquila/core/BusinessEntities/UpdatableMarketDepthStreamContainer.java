package ru.prolib.aquila.core.BusinessEntities;

public interface UpdatableMarketDepthStreamContainer extends
		MarketDepthStreamContainer
{
	
	public void update(MDUpdate update);

}
