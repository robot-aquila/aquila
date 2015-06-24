package ru.prolib.aquila.datatools.tickdatabase;

import java.io.Closeable;
import java.io.IOException;

import org.joda.time.DateTime;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.Tick;

public interface TickDatabase extends Closeable {

	public void write(SecurityDescriptor descr, Tick tick) throws IOException;
	
	public void sendMarker(DateTime date) throws IOException;
	
	public Aqiterator<Tick>
		getIterator(SecurityDescriptor descr, DateTime startingTime)
			throws IOException;
	
}
