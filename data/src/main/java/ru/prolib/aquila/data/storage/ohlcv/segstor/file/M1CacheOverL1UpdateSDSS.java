package ru.prolib.aquila.data.storage.ohlcv.segstor.file;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesL1UpdateAggregator;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileManager;

/**
 * Basic OHLCV cache based on L1 data storage.
 * <p>
 * This storage transparently builds OHLCV data using underlying Level-1 market
 * data. This low-level cache uses data of trades of day to combine data in one
 * minute bars which contain open, high, low, close prices and total bar traded
 * volume. All bars for each day will be cached to a file to speed-up further
 * operations. This low-level cache may used to build OHLCV caches for bars of
 * higher intervals. Actually it's just a shortcut to standard intraday cache
 * implementation.
 */
public class M1CacheOverL1UpdateSDSS extends IntradayCacheOverSDSS<L1Update> {

	public M1CacheOverL1UpdateSDSS(SymbolDailySegmentStorage<L1Update> sourceSegments,
			SegmentFileManager cacheManager, CacheUtils utils)
	{
		super(TimeFrame.M1, sourceSegments,
			CandleSeriesL1UpdateAggregator.getInstance(), cacheManager, utils);
	}
	
	public M1CacheOverL1UpdateSDSS(SymbolDailySegmentStorage<L1Update> sourceSegments,
			SegmentFileManager cacheManager)
	{
		this(sourceSegments, cacheManager, CacheUtils.getInstance());
	}
	
}
