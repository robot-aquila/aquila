package ru.prolib.aquila.utils.experimental.sst.msig;

public interface MarketSignalProvider {

	public MarketSignal getSignal();
	
	public void start();
	
	public void stop();
	
}
