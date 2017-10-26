package ru.prolib.aquila.data.storage.ohlcv.segstor;

import java.util.HashMap;
import java.util.Map;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFrame;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;

/**
 * Segment storage registry.
 * <p>
 * This class provides an access to set of segment storages: one storage per
 * unique time frame. It is required because building a storage of higher level
 * may require segment storages of lower level and all segments storages should
 * be accessible through one object.
 */
public class SegmentStorageRegistryImpl implements SegmentStorageRegistry {
	private final SegmentStorageFactory sdssFactory;
	private final Map<TFrame, SymbolDailySegmentStorage<Candle>> sdssRegistry;
	
	/**
	 * Constructor for testing purposes only.
	 * <p>
	 * @param sdssFactory - factory to produce daily segment storages of symbol data
	 * @param sdssRegistry - registry to store daily segment storages
	 */
	SegmentStorageRegistryImpl(SegmentStorageFactory sdssFactory,
			Map<TFrame, SymbolDailySegmentStorage<Candle>> sdssRegistry)
	{
		this.sdssFactory = sdssFactory;
		this.sdssRegistry = sdssRegistry;
	}
	
	public SegmentStorageRegistryImpl(SegmentStorageFactory sdssFactory) {
		this(sdssFactory, new HashMap<>());
	}
	
	@Override
	public synchronized SymbolDailySegmentStorage<Candle> getSDSS(TFrame tframe)
		throws DataStorageException
	{
		SymbolDailySegmentStorage<Candle> x = sdssRegistry.get(tframe);
		if ( x == null ) {
			x = sdssFactory.createSDSS(tframe);
			sdssRegistry.put(tframe, x);
		}
		return x;
	}

}
