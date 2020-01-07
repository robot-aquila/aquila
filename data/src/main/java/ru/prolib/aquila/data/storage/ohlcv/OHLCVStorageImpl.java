package ru.prolib.aquila.data.storage.ohlcv;

import java.time.Instant;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.MDStorage;

/**
 * Top-level OHLCV storage provides an access to all available data.
 * It uses underlying storage registry to delegate requests to specific storage
 * according to a passed key.
 */
public class OHLCVStorageImpl implements MDStorage<TFSymbol, Candle> {
	private OHLCVStorageRegistry registry;
	
	public OHLCVStorageImpl(OHLCVStorageRegistry registry) {
		this.registry = registry;
	}

	@Override
	public Set<TFSymbol> getKeys() throws DataStorageException {
		return registry.getStorage(ZTFrame.M1).getKeys();
	}

	@Override
	public CloseableIterator<Candle> createReader(TFSymbol key)
			throws DataStorageException
	{
		return registry.getStorage(key.getTimeFrame()).createReader(key);
	}

	@Override
	public CloseableIterator<Candle> createReaderFrom(TFSymbol key, Instant from)
			throws DataStorageException
	{
		return registry.getStorage(key.getTimeFrame()).createReaderFrom(key, from);
	}

	@Override
	public CloseableIterator<Candle> createReader(TFSymbol key, Instant from, int count)
			throws DataStorageException
	{
		return registry.getStorage(key.getTimeFrame()).createReader(key, from, count);
	}

	@Override
	public CloseableIterator<Candle> createReader(TFSymbol key, Instant from, Instant to)
			throws DataStorageException
	{
		return registry.getStorage(key.getTimeFrame()).createReader(key, from, to);
	}

	@Override
	public CloseableIterator<Candle> createReader(TFSymbol key, int count, Instant to)
			throws DataStorageException
	{
		return registry.getStorage(key.getTimeFrame()).createReader(key, count, to);
	}

	@Override
	public CloseableIterator<Candle> createReaderTo(TFSymbol key, Instant to)
			throws DataStorageException
	{
		return registry.getStorage(key.getTimeFrame()).createReaderTo(key, to);
	}

	@Override
	public void warmingUpReader(TFSymbol key, int count, Instant to) {
		
	}

}
