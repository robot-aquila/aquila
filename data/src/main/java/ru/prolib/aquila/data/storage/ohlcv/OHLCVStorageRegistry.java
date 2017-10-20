package ru.prolib.aquila.data.storage.ohlcv;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.MDStorage;

public interface OHLCVStorageRegistry {

	MDStorage<TFSymbol, Candle> getStorage(TimeFrame tframe)
		throws DataStorageException;
	
}
