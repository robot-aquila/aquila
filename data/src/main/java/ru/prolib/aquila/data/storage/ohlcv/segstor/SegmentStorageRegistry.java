package ru.prolib.aquila.data.storage.ohlcv.segstor;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFrame;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthlySegmentStorage;

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
	
	/**
	 * Get segment storage of monthly data.
	 * <p>
	 * @param tframe - time frame
	 * @return segment storage
	 * @throws DataStorageException if an error occurred
	 */
	SymbolMonthlySegmentStorage<Candle> getSMSS(TFrame tframe)
			throws DataStorageException;

}