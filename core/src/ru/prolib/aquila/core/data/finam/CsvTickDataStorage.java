package ru.prolib.aquila.core.data.finam;

import org.joda.time.DateTime;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.*;

/**
 * Хранилище сделок на основе csv-файлов формата FINAM.
 */
public class CsvTickDataStorage implements TickDataStorage {
	
	public CsvTickDataStorage() {
		super();
	}

	@Override
	public Aqiterator<Tick> getTicks(SecurityDescriptor descr, DateTime from)
			throws DataException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
