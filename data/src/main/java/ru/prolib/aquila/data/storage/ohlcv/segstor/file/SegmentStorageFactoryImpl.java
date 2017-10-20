package ru.prolib.aquila.data.storage.ohlcv.segstor.file;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesCandleAggregator;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.ohlcv.segstor.SegmentStorageFactory;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileManager;

/**
 * This factory produces segment storages to store intraday data using L1
 * update storage as a source storage and cache file manager.
 */
public class SegmentStorageFactoryImpl implements SegmentStorageFactory {
	private final SymbolDailySegmentStorage<Candle> m1SDSS;
	private final SegmentFileManager cacheManager;
	
	public SegmentStorageFactoryImpl(SymbolDailySegmentStorage<L1Update> sourceSegments,
			SegmentFileManager cacheManager)
	{
		this.m1SDSS = new M1CacheOverL1UpdateSDSS(sourceSegments, cacheManager);
		this.cacheManager = cacheManager;
	}
	
	@Override
	public SymbolDailySegmentStorage<Candle> createSDSS(TimeFrame tframe)
		throws DataStorageException
	{
		if ( tframe.equals(TimeFrame.M1) ) {
			return m1SDSS;
		}
		if ( ! tframe.isIntraday() ) {
			throw new DataStorageException("Unsupported timeframe: " + tframe);
		}
		return new IntradayCacheOverSDSS<>(tframe, m1SDSS,
				CandleSeriesCandleAggregator.getInstance(), cacheManager);
	}

}
