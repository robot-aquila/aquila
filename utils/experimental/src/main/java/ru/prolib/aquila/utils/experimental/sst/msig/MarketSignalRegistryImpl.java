package ru.prolib.aquila.utils.experimental.sst.msig;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MarketSignalRegistryImpl implements MarketSignalRegistry {
	private final Map<String, MarketSignalProvider> providers;
	
	public MarketSignalRegistryImpl() {
		providers = new LinkedHashMap<>();
	}

	@Override
	public void register(MarketSignalBuilder builder, String signalID) {
		MarketSignalProvider p;
		synchronized ( this ) {
			p = providers.get(signalID);
			if ( p != null ) {
				throw new IllegalArgumentException("Signal already exists: " + signalID);
			}
			p = builder.build(signalID);
			providers.put(signalID, p);
		}
		p.start();
	}

	@Override
	public synchronized MarketSignal getSignal(String id) {
		MarketSignalProvider p = providers.get(id);
		if ( p == null ) {
			throw new IllegalArgumentException("Signal not found: " + id);
		}
		return p.getSignal();
	}

	@Override
	public void close() {
		List<MarketSignalProvider> dummy;
		synchronized ( providers ) {
			dummy = new ArrayList<>(providers.values());
			providers.clear();
		}
		for ( MarketSignalProvider x : dummy ) {
			x.stop();
		}
	}

}
