package ru.prolib.aquila.datatools.tickdatabase.simple;

import org.joda.time.LocalDate;

/**
 * Interface of the tick data segment.
 * <p>
 * The tick data segment is an ordered set of ticks of security.
 * Each segment contains data of one day.
 */
public interface DataSegmentWriter extends DataWriter {

	/**
	 * Get a date of tick data segment.
	 * <p>
	 * @return date
	 */
	public LocalDate getDate();

}
