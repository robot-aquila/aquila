package ru.prolib.aquila.web.utils.finam.data;

import java.io.File;
import java.time.ZoneId;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.MDStorage;
import ru.prolib.aquila.data.storage.ohlcv.OHLCVStorageBuilder;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.web.utils.finam.segstor.FinamL1UpdateSegmentStorage;

/**
 * Facade of FINAM components to access to the data.
 */
public class FinamData {
	
	/**
	 * Create daily segment storage of FINAM exported trades.
	 * <p>
	 * @param root - root of data directory
	 * @return segment storage
	 */
	public SymbolDailySegmentStorage<L1Update> createSDSS(File dataRootDir) {
		return new FinamL1UpdateSegmentStorage(dataRootDir);
	}
	
	/**
	 * Create OHLCV (candlesticks) caching data storage based on FINAM exported trades.
	 * <p>
	 * @param dataRootDir - root of data directory
	 * @param cacheRootDir - root directory to store cache files. If the
	 * cache directory is not exists then it will be created.
	 * @return OHLCV data storage
	 * @throws DataStorageException if an error occurred
	 */
	public MDStorage<TFSymbol, Candle> createCachingOHLCV(File dataRootDir, File cacheRootDir)
			throws DataStorageException
	{
		cacheRootDir.mkdirs();
		return new OHLCVStorageBuilder().createCachingStorage(createSDSS(dataRootDir),
				cacheRootDir, ZoneId.of("Europe/Moscow"));
	}

}
