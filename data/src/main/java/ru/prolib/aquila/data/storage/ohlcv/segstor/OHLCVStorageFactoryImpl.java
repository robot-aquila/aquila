package ru.prolib.aquila.data.storage.ohlcv.segstor;

import java.time.ZoneId;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.MDStorage;
import ru.prolib.aquila.data.storage.ohlcv.OHLCVStorageFactory;

/**
 * This factory produces OHLCV storages of different timeframes based on
 * segment storage registry. This factory produces a middle-class objects to
 * use in top-level MD storage.
 */
public class OHLCVStorageFactoryImpl implements OHLCVStorageFactory {
	private final SegmentStorageRegistry storageRegistry;
	private final ZoneId zoneID;
	
	/**
	 * Constructor.
	 * <p>
	 * @param storageRegistry - segment storage registry
	 * @param zoneID - time zone ID to convert UTC time to the date of segment
	 */
	public OHLCVStorageFactoryImpl(SegmentStorageRegistry storageRegistry, ZoneId zoneID) {
		this.storageRegistry = storageRegistry;
		this.zoneID = zoneID;
	}
	
	@Override
	public MDStorage<TFSymbol, Candle> createStorage(TimeFrame tframe)
			throws DataStorageException
	{
		if ( tframe.isIntraday() ) {
			return new IntradayMDStorageOverSDSS(storageRegistry.getSDSS(tframe), zoneID, tframe);
		}
		throw new DataStorageException("Unsupported timeframe: " + tframe);

	}

}
