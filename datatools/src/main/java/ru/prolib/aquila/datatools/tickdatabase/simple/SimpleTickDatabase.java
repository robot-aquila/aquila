package ru.prolib.aquila.datatools.tickdatabase.simple;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.tickdatabase.TickDatabase;

public class SimpleTickDatabase implements TickDatabase {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SimpleTickDatabase.class);
	}
	
	private final DataSegmentManager manager;
	private final Map<SecurityDescriptor, DataSegment> segments;
	
	public SimpleTickDatabase(DataSegmentManager manager,
			Map<SecurityDescriptor, DataSegment> segments)
	{
		super();
		this.manager = manager;
		this.segments = segments;
	}
	
	public SimpleTickDatabase(DataSegmentManager manager) {
		this(manager, new Hashtable<SecurityDescriptor, DataSegment>());
	}

	@Override
	public void write(SecurityDescriptor descr, Tick tick)
			throws IOException
	{
		LocalDate date = tick.getTime().toLocalDate();
		DataSegment segment = segments.get(descr);
		if ( segment != null ) {
			if ( segment.getDate().equals(date) == false ) {
				manager.closeSegment(segment);
				segment = null;
			}
		}
		if ( segment == null ) {
			segment = manager.openSegment(descr, date);
			segments.put(descr, segment);
		}
		segment.write(tick);
	}

	@Override
	public Aqiterator<Tick>
		getIterator(SecurityDescriptor descr, DateTime startingTime)
			throws IOException
	{
		throw new IOException("Not implemented");
	}

	@Override
	public void close() {
		for ( DataSegment segment : segments.values() ) {
			try {
				manager.closeSegment(segment);
			} catch ( IOException e ) {
				logger.error("Error closing data segment: ", e);
			}
		}
		segments.clear();
	}

	@Override
	public void sendMarker(DateTime date) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
