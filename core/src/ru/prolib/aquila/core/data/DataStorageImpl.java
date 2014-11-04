package ru.prolib.aquila.core.data;

import org.joda.time.DateTime;
import ru.prolib.aquila.core.BusinessEntities.*;

public class DataStorageImpl implements DataStorage {
	private TickIteratorStorage iteratorStorage;
	private TickTemporalStorage temporalStorage;
	
	public DataStorageImpl() {
		super();
	}
	
	public void setIteratorStorage(TickIteratorStorage storage) {
		this.iteratorStorage = storage;
	}
	
	public void setTemporalStorage(TickTemporalStorage storage) {
		this.temporalStorage = storage;
	}

	@Override
	public Aqiterator<Tick> getIterator(String dataId, DateTime start)
			throws DataException
	{
		return iteratorStorage.getIterator(dataId, start);
	}

	@Override
	public Aqiterator<Tick> getIterator(SecurityDescriptor descr,
			DateTime start) throws DataException
	{
		return iteratorStorage.getIterator(descr, start);
	}

	@Override
	public Aqtemporal<Tick> getTemporal(CurrencyPair pair)
			throws DataException
	{
		return temporalStorage.getTemporal(pair);
	}

	@Override
	public Aqtemporal<Tick> getTemporal(String dataId) throws DataException {
		return temporalStorage.getTemporal(dataId);
	}

}
