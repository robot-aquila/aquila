package ru.prolib.aquila.data.storage.ohlcv.segstor;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFrame;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;

public interface SegmentStorageFactory {

	/**
	 * Create daily segment storage of symbol data.
	 * <p>
	 * @param tframe - time frame of the storage
	 * @return segment storage
	 * @throws DataStorageException if an error occurred
	 */
	SymbolDailySegmentStorage<Candle> createSDSS(TFrame tframe)
			throws DataStorageException;

}