package ru.prolib.aquila.data.storage.ohlcv;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.MDStorage;

public interface OHLCVStorageFactory {

	MDStorage<TFSymbol, Candle> createStorage(TimeFrame tframe)
		throws DataStorageException;
	
}
