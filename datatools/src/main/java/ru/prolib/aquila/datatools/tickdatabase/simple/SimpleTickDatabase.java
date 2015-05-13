package ru.prolib.aquila.datatools.tickdatabase.simple;

import java.util.Hashtable;
import java.util.Map;

import org.joda.time.LocalDateTime;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.GeneralException;
import ru.prolib.aquila.datatools.tickdatabase.TickDatabase;

public class SimpleTickDatabase implements TickDatabase {
	private final DataWriterFactory factory;
	private final Map<SecurityDescriptor, DataWriter> segments;
	
	public SimpleTickDatabase(DataWriterFactory factory,
			Map<SecurityDescriptor, DataWriter> segments)
	{
		super();
		this.factory = factory;
		this.segments = segments;
	}
	
	public SimpleTickDatabase(DataWriterFactory factory) {
		this(factory, new Hashtable<SecurityDescriptor, DataWriter>());
	}

	@Override
	public void write(SecurityDescriptor descr, Tick tick)
			throws GeneralException
	{
		DataWriter writer = segments.get(descr);
		if ( writer == null ) {
			writer = factory.createWriter(descr);
			segments.put(descr, writer);
		}
		writer.write(tick);
	}

	@Override
	public Aqiterator<Tick> getIterator(SecurityDescriptor descr,
			LocalDateTime startingTime) throws GeneralException
	{
		throw new GeneralException("Not implemented");
	}

	@Override
	public void close() {
		for ( DataWriter writer : segments.values() ) {
			writer.close();
		}
		segments.clear();
	}

}
