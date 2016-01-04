package ru.prolib.aquila.datatools.tickdatabase.simple;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.SimpleIterator;
import ru.prolib.aquila.datatools.tickdatabase.TickDatabase;

public class SimpleTickDatabase implements TickDatabase {
	private static final Logger logger;
	private static final ZoneOffset zone;
	
	static {
		logger = LoggerFactory.getLogger(SimpleTickDatabase.class);
		zone = ZoneOffset.UTC;
	}
	
	private final DataSegmentManager manager;
	private final Map<Symbol, DataSegment> segments;
	
	public SimpleTickDatabase(DataSegmentManager manager,
			Map<Symbol, DataSegment> segments)
	{
		super();
		this.manager = manager;
		this.segments = segments;
	}
	
	public SimpleTickDatabase(DataSegmentManager manager) {
		this(manager, new Hashtable<Symbol, DataSegment>());
	}

	@Override
	public void write(Symbol symbol, Tick tick) throws IOException {
		LocalDate date = LocalDateTime.ofInstant(tick.getTime(), zone)
				.toLocalDate();
		DataSegment segment = segments.get(symbol);
		if ( segment != null ) {
			if ( segment.getDate().equals(date) == false ) {
				manager.closeSegment(segment);
				segment = null;
			}
		}
		if ( segment == null ) {
			segment = manager.openSegment(symbol, date);
			segments.put(symbol, segment);
		}
		segment.write(tick);
	}

	@Override
	public Aqiterator<Tick> getTicks(Symbol symbol, Instant startingTime)
			throws IOException
	{
		return manager.isDataAvailable(symbol) ?
				new SeamlessTickReader(symbol, startingTime, manager) :
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
	public void sendMarker(Instant time) throws IOException {
		LocalDate date = LocalDateTime.ofInstant(time, zone).toLocalDate();
		List<Symbol> keys = new LinkedList<Symbol>(segments.keySet());
		for ( Symbol symbol : keys ) {
			DataSegment segment = segments.get(symbol);
			if ( date.compareTo(segment.getDate()) >= 0 ) {
				segments.remove(symbol);
				try {
					manager.closeSegment(segment);
				} catch ( IOException e ) {
					logger.error("Error closing data segment: ", e);	
				}
			}
		}
		
	}

	@Override
	public Aqiterator<Tick> getTicks(Symbol symbol, int numLastSegments)
			throws IOException
	{
		List<LocalDate> list = manager.getSegmentList(symbol);
		int size = list.size();
		if ( size == 0 ) {
			return new SimpleIterator<Tick>(new Vector<Tick>());
		} else if ( size <= numLastSegments ) {
			return new SeamlessTickReader(symbol,
					list.get(0).atStartOfDay().toInstant(zone), manager);
		} else {
			return new SeamlessTickReader(symbol, list.get(size - numLastSegments)
					.atStartOfDay().toInstant(zone), manager);
		}
	}

}
