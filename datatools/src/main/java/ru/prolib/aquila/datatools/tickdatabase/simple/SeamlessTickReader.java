package ru.prolib.aquila.datatools.tickdatabase.simple;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.DataException;

public class SeamlessTickReader implements Aqiterator<Tick> {
	private static final Logger logger;
	private static final ZoneOffset zone;
	
	static {
		logger = LoggerFactory.getLogger(SeamlessTickReader.class);
		zone = ZoneOffset.UTC;
	}
	
	private final Symbol symbol;
	private final DataSegmentManager segmentManager;
	private Instant currentTime;
	private Aqiterator<Tick> currentSegment;
	
	public SeamlessTickReader(Symbol symbol,
			Instant startingTime, DataSegmentManager segmentManager)
	{
		super();
		this.symbol = symbol;
		this.segmentManager = segmentManager;
		currentTime = startingTime;
	}

	@Override
	public void close() {
		closeCurrentSegment();
	}
	
	/**
	 * Get current time pointer.
	 * <p>
	 * @return time
	 */
	public Instant getCurrentTime() {
		return currentTime;
	}
	
	/**
	 * Get symbol info.
	 * <p>
	 * @return symbol info
	 */
	public Symbol getSymbol() {
		return symbol;
	}
	
	/**
	 * @Get data segment manager.
	 * <p>
	 * @return data segment manager
	 */
	public DataSegmentManager getDataSegmentManager() {
		return segmentManager;
	}
	
	@Override
	public Tick item() throws DataException {
		return currentSegment.item();
	}

	@Override
	public boolean next() throws DataException {
		for (;;) {
			if ( currentSegment == null ) {
				if ( ! openNextSegment() ) {
					return false;
				}
			}
			if ( ! currentSegment.next() ) {
				// Cases:
				// 1) Segment just opened and empty;
				// 2) End of segment was reached;
				// Move current time pointer to the next date and try open next.
				currentTime = LocalDateTime.ofInstant(currentTime, zone)
						.toLocalDate().plusDays(1).atStartOfDay().toInstant(zone);
				closeCurrentSegment();
				continue;
			}
			// We may have some ticks to skip if they are before current time
			do {
				Instant nextTime = currentSegment.item().getTime(); 
				if ( ! nextTime.isBefore(currentTime) ) {
					currentTime = nextTime;
					return true;
				}
			} while ( currentSegment.next() );
			// Current segment is ended
		}
	}
	
	/**
	 * Open next segment for reading.
	 * <p>
	 * @return true if segment opened, false if no more segments available
	 */
	private boolean openNextSegment() {
		try {
			LocalDate date = LocalDateTime.ofInstant(currentTime, zone)
					.toLocalDate();
			if ( ! segmentManager.isDataAvailable(symbol, date) ) {
				date = segmentManager.getDateOfNextSegment(symbol, date);
				if ( date == null ) {
					return false;
				}
				currentTime = date.atStartOfDay().toInstant(zone);
			}
			currentSegment = segmentManager.openReader(symbol, date);
			return true;
		} catch ( IOException e ) {
			logger.error("Error opening segment: ", e);
			return false;
		}
	}
	
	private void closeCurrentSegment() {
		if ( currentSegment != null ) {
			try {
				segmentManager.closeReader(currentSegment);	
			} catch ( IOException e ) {
				logger.error("Closing segment error: ", e);
			}
			currentSegment = null;
		}
	}

}
