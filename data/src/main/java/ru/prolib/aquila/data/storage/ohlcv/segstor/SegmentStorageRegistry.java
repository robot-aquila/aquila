package ru.prolib.aquila.data.storage.ohlcv.segstor;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;

public interface SegmentStorageRegistry {

	/**
	 * Get daily segment storage of symbol data.
	 * <p>
	 * @param tframe - timeframe associated with the storage
	 * @return segment storage
	 * @throws DataStorageException if an error occurred
	 */
	SymbolDailySegmentStorage<Candle> getSDSS(TimeFrame tframe)
			throws DataStorageException;

}