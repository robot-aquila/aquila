package ru.prolib.aquila.data.storage.ohlcv.segstor;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.MDStorage;
import ru.prolib.aquila.data.storage.ohlcv.OHLCVStorageFactory;

/**
 * This factory produces OHLCV storages of different time frames based on
 * segment storage registry. This factory produces a middle-class objects to
 * use in top-level MD storage.
 */
public class OHLCVStorageFactoryImpl implements OHLCVStorageFactory {
	private final SegmentStorageRegistry storageRegistry;
	
	/**
	 * Constructor.
	 * <p>
	 * @param storageRegistry - segment storage registry
	 */
	public OHLCVStorageFactoryImpl(SegmentStorageRegistry storageRegistry) {
		this.storageRegistry = storageRegistry;
	}
	
	@Override
	public MDStorage<TFSymbol, Candle> createStorage(ZTFrame tframe)
			throws DataStorageException
	{
		if ( tframe.isIntraday() ) {
			return new IntradayMDStorageOverSDSS(storageRegistry.getSDSS(tframe.toTFrame()), tframe);
		}
		throw new DataStorageException("Unsupported timeframe: " + tframe);
	}

}
