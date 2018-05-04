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
import ru.prolib.aquila.probe.datasim.L1UpdateSourceSATImpl;
import ru.prolib.aquila.web.utils.finam.datasim.FinamL1UpdateReaderFactory;
import ru.prolib.aquila.web.utils.finam.segstor.FinamL1UpdateSegmentStorage;

/**
 * Facade to access data in FINAM export format.
 */
public class FinamData {
	
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
	
	/**
	 * Create data source to replay L1 updates.
	 * <p>
	 * This method produces a data source which will read data in FINAM export format,
	 * convert it to L1 updates and schedule to reproduce as tick data using scheduler.
	 * All containers passed to produced source must implement EditableSecurity
	 * interface (or you'll get an exception). This is because FINAM exported data does
	 * not contain some required information about symbol attributes. Such information
	 * will be obtained directly from EditableSecurity instance.
	 * <p>
	 * @param dataRootDir - root of data directory
	 * @param scheduler - instance of scheduler to replay tick data
	 * @param scaleDB - database of scale of symbol price
	 * @return instance of L1 updates data source
	 */
	public L1UpdateSource createL1UpdateSource(File dataRootDir, Scheduler scheduler, PriceScaleDB scaleDB) {
		return new L1UpdateSourceSATImpl(new L1UpdateSourceImpl(scheduler, new FinamL1UpdateReaderFactory(dataRootDir, scaleDB)));
	}

}
