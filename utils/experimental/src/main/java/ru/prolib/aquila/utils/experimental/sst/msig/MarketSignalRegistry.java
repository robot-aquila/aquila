package ru.prolib.aquila.utils.experimental.sst.msig;

public interface MarketSignalRegistry {
	
	void register(MarketSignalBuilder builder, String signalID);
	
	void register(MarketSignalProvider provider);
	
	MarketSignal getSignal(String id);
	
	void close();
	
	void remove(String signalID);

}
