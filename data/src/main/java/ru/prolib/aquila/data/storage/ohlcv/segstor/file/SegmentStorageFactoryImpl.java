package ru.prolib.aquila.data.storage.ohlcv.segstor.file;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFrame;
import ru.prolib.aquila.core.data.timeframe.TFMinutes;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesCandleAggregator;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.ohlcv.segstor.SegmentStorageFactory;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileManager;

/**
 * This factory produces segment storages to store intraday data using L1
 * update storage as a source storage and cache file manager. All produced
 * storages are in same time zone - the time zone of source segment storage.
 */
public class SegmentStorageFactoryImpl implements SegmentStorageFactory {
	private final SymbolDailySegmentStorage<L1Update> sourceSegments;
	private final SegmentFileManager cacheManager;
	private SymbolDailySegmentStorage<Candle> m1storage;
	
	public SegmentStorageFactoryImpl(SymbolDailySegmentStorage<L1Update> sourceSegments,
			SegmentFileManager cacheManager)
	{
		this.sourceSegments = sourceSegments;
		this.cacheManager = cacheManager;
	}
	
	@Override
	public synchronized SymbolDailySegmentStorage<Candle> createSDSS(TFrame tframe)
		throws DataStorageException
	{
		if ( ! tframe.isIntraday() ) {
			throw new DataStorageException("Unsupported timeframe: " + tframe);
		}
		if ( tframe.equals(new TFMinutes(1)) ) {
			return getM1SDSS();
		}
		return new IntradayCacheOverSDSS<>(tframe, getM1SDSS(),
				CandleSeriesCandleAggregator.getInstance(), cacheManager);
	}
	
	private SymbolDailySegmentStorage<Candle> getM1SDSS() {
		if ( m1storage == null ) {
			m1storage = new M1CacheOverL1UpdateSDSS(sourceSegments, cacheManager);
		}
		return m1storage;
	}

}
