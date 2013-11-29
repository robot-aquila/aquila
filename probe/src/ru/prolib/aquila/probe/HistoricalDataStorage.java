package ru.prolib.aquila.probe;

import org.joda.time.Interval;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;

public interface HistoricalDataStorage<T> {

	public HistoricalDataReader<T>
		createReader(SecurityDescriptor descr, Interval interval)
			throws HistoricalDataException;
	
}
