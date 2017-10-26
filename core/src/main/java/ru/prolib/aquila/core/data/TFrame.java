package ru.prolib.aquila.core.data;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * Timeframe interface.
 */
public interface TFrame {

	/**
	 * Check that the timeframe is an intraday timeframe.
	 * <p>
	 * @return true if intraday
	 */
	boolean isIntraday();
	
	/**
	 * Get time unit of this timeframe.
	 * <p>
	 * @return time unit
	 */
	ChronoUnit getUnit();
	
	/**
	 * Get length of period in units.
	 * <p>
	 * @return length
	 */
	int getLength();
	
	/**
	 * Convert this timeframe to a zoned timeframe.
	 * <p>
	 * @param zoneID - zone to use
	 * @return a zoned time frame instance
	 */
	ZTFrame toZTFrame(ZoneId zoneID);
	
}
