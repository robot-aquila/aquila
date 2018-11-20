package ru.prolib.aquila.data.storage.ohlcv.segstor;

import java.util.HashMap;
import java.util.Map;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFrame;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthlySegmentStorage;

/**
 * Segment storage registry.
 * <p>
 * This class provides an access to set of segment storages: one storage per
 * unique time frame. It is required because building a storage of higher level
 * may require segment storages of lower level and all segments storages should
 * be accessible through one object.
 */
public class SegmentStorageRegistryImpl implements SegmentStorageRegistry {
	private final SegmentStorageFactory factory;
	private final Map<TFrame, SymbolDailySegmentStorage<Candle>> sdssRegistry;
	private final Map<TFrame, SymbolMonthlySegmentStorage<Candle>> smssRegistry;
	
	/**
	 * Constructor for testing purposes only.
	 * <p>
	 * @param factory - factory to produce segment storages of symbol data
	 * @param sdssRegistry - registry to store daily segment storages
	 * @param smssRegistry - registry to store monthly segment storages
	 */
	SegmentStorageRegistryImpl(SegmentStorageFactory factory,
			Map<TFrame, SymbolDailySegmentStorage<Candle>> sdssRegistry,
			Map<TFrame, SymbolMonthlySegmentStorage<Candle>> smssRegistry)
	{
		this.factory = factory;
		this.sdssRegistry = sdssRegistry;
		this.smssRegistry = smssRegistry;
	}
	
	public SegmentStorageRegistryImpl(SegmentStorageFactory factory) {
		this(factory, new HashMap<>(), new HashMap<>());
	}
	
	@Override
	public synchronized SymbolDailySegmentStorage<Candle> getSDSS(TFrame tframe)
		throws DataStorageException
	{
		SymbolDailySegmentStorage<Candle> x = sdssRegistry.get(tframe);
		if ( x == null ) {
			x = factory.createSDSS(tframe);
			sdssRegistry.put(tframe, x);
		}
		return x;
	}

	@Override
	public synchronized SymbolMonthlySegmentStorage<Candle> getSMSS(TFrame tframe)
		throws DataStorageException
	{
		SymbolMonthlySegmentStorage<Candle> x = smssRegistry.get(tframe);
		if ( x == null ) {
			x = factory.createSMSS(tframe);
			smssRegistry.put(tframe, x);
		}
		return x;
	}

}
