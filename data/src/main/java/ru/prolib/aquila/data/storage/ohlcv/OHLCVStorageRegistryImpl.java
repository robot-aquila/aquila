package ru.prolib.aquila.data.storage.ohlcv;

import java.util.HashMap;
import java.util.Map;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.MDStorage;

/**
 * OHLCV storage registry.
 * <p>
 * This class provides an access to set of OHLCV data storages: one storage per
 * unique timeframe. It used from top-level storage to provide an access to
 * OHLCV data of any possible timeframe.
 */
public class OHLCVStorageRegistryImpl implements OHLCVStorageRegistry {
	private final OHLCVStorageFactory factory;
	private final Map<TimeFrame, MDStorage<TFSymbol, Candle>> registry;
	
	/**
	 * Service constructor. For testing purposes only.
	 * <p>
	 * @param factory - storage factory
	 * @param registry - storage registry map
	 */
	OHLCVStorageRegistryImpl(OHLCVStorageFactory factory,
			Map<TimeFrame, MDStorage<TFSymbol, Candle>> registry)
	{
		this.factory = factory;
		this.registry = registry;
	}
	
	public OHLCVStorageRegistryImpl(OHLCVStorageFactory factory) {
		this(factory, new HashMap<>());
	}

	@Override
	public synchronized MDStorage<TFSymbol, Candle> getStorage(TimeFrame tframe)
			throws DataStorageException
	{
		MDStorage<TFSymbol, Candle> x = registry.get(tframe);
		if ( x == null ) {
			x = factory.createStorage(tframe);
			registry.put(tframe, x);
		}
		return x;
	}

}
