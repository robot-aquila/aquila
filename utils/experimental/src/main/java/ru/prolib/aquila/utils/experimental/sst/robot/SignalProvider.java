package ru.prolib.aquila.utils.experimental.sst.robot;

public interface SignalProvider {

	public MarketSignal getSignal();
	
	public void start();
	
	public void stop();
	
}
