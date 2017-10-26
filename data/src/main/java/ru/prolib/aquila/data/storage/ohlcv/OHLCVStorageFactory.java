package ru.prolib.aquila.data.storage.ohlcv;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.MDStorage;

public interface OHLCVStorageFactory {

	MDStorage<TFSymbol, Candle> createStorage(ZTFrame tframe)
		throws DataStorageException;
	
}
