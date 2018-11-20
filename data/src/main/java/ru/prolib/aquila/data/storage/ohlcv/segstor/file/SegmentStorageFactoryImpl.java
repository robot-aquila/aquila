package ru.prolib.aquila.data.storage.ohlcv.segstor.file;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFrame;
import ru.prolib.aquila.core.data.timeframe.TFDays;
import ru.prolib.aquila.core.data.timeframe.TFHours;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesAggregator;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesCandleAggregator;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.ohlcv.segstor.SegmentStorageFactory;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthlySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileManager;

/**
 * This factory produces segment storages to store intraday data using L1
 * update storage as a source storage and cache file manager. All produced
 * storages are in same time zone - the time zone of source segment storage.
 */
public class SegmentStorageFactoryImpl implements SegmentStorageFactory {
	static private final CandleSeriesAggregator<Candle> aggregator;
	
	static {
		 aggregator = CandleSeriesCandleAggregator.getInstance();
	}
	
	private final SymbolDailySegmentStorage<L1Update> sdss;
	private final SegmentFileManager cacheManager;
	private SymbolDailySegmentStorage<Candle> m1storage, h1storage;
	
	public SegmentStorageFactoryImpl(SymbolDailySegmentStorage<L1Update> sdss,
			SegmentFileManager cacheManager)
	{
		this.sdss = sdss;
		this.cacheManager = cacheManager;
	}
	
	synchronized SymbolDailySegmentStorage<Candle> getM1SDSS() {
		if ( m1storage == null ) {
			m1storage = new M1CacheOverL1UpdateSDSS(sdss, cacheManager);
		}
		return m1storage;
	}
	
	synchronized SymbolDailySegmentStorage<Candle> getH1SDSS() {
		if ( h1storage == null ) {
			h1storage = new IntradayCacheOverSDSS<>(new TFHours(1),
					getM1SDSS(),
					aggregator,
					cacheManager
				);
		}
		return h1storage;
	}
	
	@Override
	public synchronized SymbolDailySegmentStorage<Candle> createSDSS(TFrame tframe)
		throws DataStorageException
	{
		switch ( tframe.getUnit() ) {
		case MINUTES:
			if ( tframe.getLength() == 1 ) {
				return getM1SDSS();
			} else {
				return new IntradayCacheOverSDSS<>(tframe,
						getM1SDSS(),
						aggregator,
						cacheManager
					);
			}
		case HOURS:
			if ( tframe.getLength() == 1 ) {
				return getH1SDSS();
			} else {
				return new IntradayCacheOverSDSS<>(tframe,
						getH1SDSS(),
						aggregator,
						cacheManager
					);
			}
		default:
			throw new DataStorageException("Unsupported timeframe: " + tframe);
		}
	}
	
	@Override
	public SymbolMonthlySegmentStorage<Candle> createSMSS(TFrame tframe)
			throws DataStorageException
	{
		switch ( tframe.getUnit() ) {
		case DAYS:
			return new D1CacheOverCandleSDSS(tframe,
					getH1SDSS(),
					aggregator,
					cacheManager
				);
		default:
			throw new DataStorageException("Unsupported timeframe: " + tframe);
		}
	}

}
