package ru.prolib.aquila.datatools.tickdatabase.simple;

import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.SimpleIterator;
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
		getTicks(SecurityDescriptor descr, DateTime startingTime)
			throws IOException
	{
		return manager.isDataAvailable(descr) ?
				new SeamlessTickReader(descr, startingTime, manager) :
				new SimpleIterator<Tick>(new Vector<Tick>());
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
	public void sendMarker(DateTime time) throws IOException {
		LocalDate date = time.toLocalDate();
		List<SecurityDescriptor>
			keys = new LinkedList<SecurityDescriptor>(segments.keySet());
		for ( SecurityDescriptor descr : keys ) {
			DataSegment segment = segments.get(descr);
			if ( date.compareTo(segment.getDate()) >= 0 ) {
				segments.remove(descr);
				try {
					manager.closeSegment(segment);
				} catch ( IOException e ) {
					logger.error("Error closing data segment: ", e);	
				}
			}
		}
		
	}

	@Override
	public Aqiterator<Tick>
		getTicks(SecurityDescriptor descr, int numLastSegments)
			throws IOException
	{
		List<LocalDate> list = manager.getSegmentList(descr);
		int size = list.size();
		if ( size == 0 ) {
			return new SimpleIterator<Tick>(new Vector<Tick>());
		} else if ( size <= numLastSegments ) {
			return new SeamlessTickReader(descr,
					list.get(0).toDateTimeAtStartOfDay(), manager);
		} else {
			return new SeamlessTickReader(descr,
					list.get(size - numLastSegments).toDateTimeAtStartOfDay(),
					manager);
		}
	}

}
