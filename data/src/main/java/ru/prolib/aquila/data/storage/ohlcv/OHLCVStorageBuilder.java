package ru.prolib.aquila.data.storage.ohlcv;

import java.io.File;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.MDStorage;
import ru.prolib.aquila.data.storage.ohlcv.segstor.OHLCVStorageFactoryImpl;
import ru.prolib.aquila.data.storage.ohlcv.segstor.SegmentStorageRegistryImpl;
import ru.prolib.aquila.data.storage.ohlcv.segstor.file.SegmentStorageFactoryImpl;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileManager;
import ru.prolib.aquila.data.storage.segstor.file.V1SegmentFileManagerImpl;

/**
 * Constructor of OHLCV storages.
 * <p>
 * Use this class to build different types of OHLCV storages.
 */
public class OHLCVStorageBuilder {
	
	public MDStorage<TFSymbol, Candle>
		createCachingStorage(SymbolDailySegmentStorage<L1Update> tradesStorage,
				SegmentFileManager cacheManager)
			throws DataStorageException
	{
		return new OHLCVStorageImpl(
			new OHLCVStorageRegistryImpl(
				new OHLCVStorageFactoryImpl(
					new SegmentStorageRegistryImpl(
						new SegmentStorageFactoryImpl(tradesStorage, cacheManager)
					)
				)
			)
		);
	}

	/**
	 * Create OHLCV storage based on trades and common implementation of segment file manager.
	 * <p>
	 * This method uses
	 * {@link ru.prolib.aquila.data.storage.segstor.file.V1SegmentFileManagerImpl V1SegmentFileManagerImpl}
	 * implementation to provide caching functions.
	 * <p>
	 * @param tradesStorage - underlying storage containing trades which will
	 * be used to build aggregated OHLCV data
	 * @param cacheRootDir - the cache root directory is used to save cache files
	 * @return OHLCV data storage
	 * @throws DataStorageException if an error occurred
	 */
	public MDStorage<TFSymbol, Candle>
		createCachingStorage(SymbolDailySegmentStorage<L1Update> tradesStorage,
				File cacheRootDir)
			throws DataStorageException
	{
		return createCachingStorage(tradesStorage, new V1SegmentFileManagerImpl(cacheRootDir));
	}

}
