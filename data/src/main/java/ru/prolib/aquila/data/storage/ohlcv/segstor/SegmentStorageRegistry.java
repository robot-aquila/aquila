package ru.prolib.aquila.data.storage.ohlcv.segstor;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFrame;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;

public interface SegmentStorageRegistry {

	/**
	 * Get segment storage of daily data.
	 * <p>
	 * @param tframe - time frame
	 * @return segment storage
	 * @throws DataStorageException if an error occurred
	 */
	SymbolDailySegmentStorage<Candle> getSDSS(TFrame tframe)
			throws DataStorageException;

}