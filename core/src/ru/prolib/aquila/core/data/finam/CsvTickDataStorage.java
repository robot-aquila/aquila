package ru.prolib.aquila.core.data.finam;

import org.joda.time.DateTime;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.DataException;
import ru.prolib.aquila.core.data.TickDataStorage;
import ru.prolib.aquila.core.data.TickReader;

/**
 * Хранилище сделок на основе csv-файлов формата FINAM.
 */
public class CsvTickDataStorage implements TickDataStorage {
	
	public CsvTickDataStorage() {
		super();
	}

	@Override
	public TickReader getTicks(SecurityDescriptor descr, DateTime from)
			throws DataException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
