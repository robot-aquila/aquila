package ru.prolib.aquila.datatools.tickdatabase;

import org.joda.time.LocalDateTime;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.GeneralException;

public interface TickDatabase {

	public void write(SecurityDescriptor descr, Tick tick)
			throws GeneralException;
	
	public Aqiterator<Tick> getIterator(SecurityDescriptor descr,
			LocalDateTime startingTime) throws GeneralException;
	
	public void close();
	
}
