package ru.prolib.aquila.datatools.tickdatabase;

import java.io.Closeable;
import java.io.IOException;

import org.joda.time.DateTime;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.Tick;

/**
 * Tick database interface.
 */
public interface TickDatabase extends Closeable {

	/**
	 * Write tick to the database.
	 * <p>
	 * Use this method for writing consecutive ticks to the database.
	 * <p>
	 * @param symbol - symbol info
	 * @param tick - tick
	 * @throws IOException - IO error. Also may thrown when tick from the past. 
	 */
	public void write(Symbol symbol, Tick tick) throws IOException;
	
	/**
	 * Send time marker.
	 * <p>
	 * The time marker is used for inform database of current time when tick
	 * data doesn't available. It allows to close currently opened segments
	 * which was tied to previous date or make some actions linked with time.
	 * <p>
	 * @param time - time marker value
	 * @throws IOException - IO Error.
	 */
	public void sendMarker(DateTime time) throws IOException;
	
	/**
	 * Get ticks.
	 * <p>
	 * @param symbol - symbol info
	 * @param startingTime - time of start data
	 * @return tick iterator
	 * @throws IOException - IO error
	 */
	public Aqiterator<Tick> getTicks(Symbol symbol, DateTime startingTime) throws IOException;
	
	/**
	 * Get ticks.
	 * <p>
	 * @param symbol - symbol info
	 * @param numLastSegments - number of daily segments from end to include
	 * @return tick iterator
	 * @throws IOException - IO error
	 */
	public Aqiterator<Tick> getTicks(Symbol symbol, int numLastSegments) throws IOException;
	
}
