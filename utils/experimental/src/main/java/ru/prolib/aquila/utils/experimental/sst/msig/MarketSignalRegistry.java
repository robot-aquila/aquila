package ru.prolib.aquila.utils.experimental.sst.msig;

public interface MarketSignalRegistry {
	
	void register(MarketSignalBuilder builder, String signalID);
	
	MarketSignal getSignal(String id);
	
	void close();

}
