package ru.prolib.aquila.web.utils.finam.data;

import java.io.File;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.utils.PriceScaleDB;
import ru.prolib.aquila.data.L1UpdateSource;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.MDStorage;
import ru.prolib.aquila.data.storage.ohlcv.OHLCVStorageBuilder;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.probe.datasim.L1UpdateSourceImpl;
import ru.prolib.aquila.probe.datasim.l1.L1UpdateReaderFactory;
import ru.prolib.aquila.web.utils.finam.datasim.FinamL1UpdateReaderFactory;
import ru.prolib.aquila.web.utils.finam.segstor.FinamL1UpdateSegmentStorage;
import ru.prolib.aquila.web.utils.moex.data.MoexData;

/**
 * Facade to access data in FINAM export format.
 */
public class FinamData {
	
	static PriceScaleDB createMoexPriceScaleDB(File data_dir) {
		return new MoexData().createScaleDB(data_dir);
	}
	
	/**
	 * Create daily segment storage of FINAM exported trades.
	 * <p>
	 * @param root - root of data directory
	 * @param scaleDB - database of scale of symbol price
	 * @return segment storage
	 */
	public SymbolDailySegmentStorage<L1Update> createSDSS(File dataRootDir,
			PriceScaleDB scaleDB)
	{
		return new FinamL1UpdateSegmentStorage(dataRootDir, scaleDB);
	}
	
	/**
	 * Create OHLCV (candlesticks) caching data storage based on FINAM exported trades.
	 * <p>
	 * @param dataRootDir - root of data directory
	 * @param cacheRootDir - root directory to store cache files. If the
	 * cache directory is not exists then it will be created.
	 * @param scaleDB - database of scale of symbol price
	 * @return OHLCV data storage
	 * @throws DataStorageException if an error occurred
	 */
	public MDStorage<TFSymbol, Candle> createCachingOHLCV(File dataRootDir,
			File cacheRootDir, PriceScaleDB scaleDB)
					throws DataStorageException
	{
		cacheRootDir.mkdirs();
		return new OHLCVStorageBuilder()
			.createCachingStorage(createSDSS(dataRootDir, scaleDB), cacheRootDir);
	}
	
	public MDStorage<TFSymbol, Candle> createCachingOHLCV(File data_dir, File cache_dir) throws DataStorageException {
		return createCachingOHLCV(data_dir, cache_dir, createMoexPriceScaleDB(data_dir));
	}
	
	public L1UpdateReaderFactory createUpdateReaderFactory(File data_root_dir) {
		return new FinamL1UpdateReaderFactory(data_root_dir, createMoexPriceScaleDB(data_root_dir));
	}
	
	/**
	 * Create data source to replay L1 updates.
	 * <p>
	 * This method produces a data source which will read data in FINAM export format,
	 * convert it to L1 updates and schedule to reproduce as tick data using scheduler.
	 * <p>
	 * @param dataRootDir - root of data directory
	 * @param scheduler - instance of scheduler to replay tick data
	 * @param scaleDB - database of scale of symbol price
	 * @return instance of L1 updates data source
	 */
	public L1UpdateSource createL1UpdateSource(File dataRootDir, Scheduler scheduler, PriceScaleDB scaleDB) {
		return new L1UpdateSourceImpl(scheduler, new FinamL1UpdateReaderFactory(dataRootDir, scaleDB));
	}
	
	public L1UpdateSource createL1UpdateSource(File data_dir, Scheduler scheduler) {
		return createL1UpdateSource(data_dir, scheduler, createMoexPriceScaleDB(data_dir));
	}

}
