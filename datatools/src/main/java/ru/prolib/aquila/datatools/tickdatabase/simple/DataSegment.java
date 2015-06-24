package ru.prolib.aquila.datatools.tickdatabase.simple;

import org.joda.time.LocalDate;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.datatools.tickdatabase.TickWriter;

/**
 * Interface of the tick data segment.
 * <p>
 * The tick data segment is an ordered set of ticks of security.
 * Each segment contains data of one day.
 */
public interface DataSegment extends TickWriter {
	
	/**
	 * Get security descriptor related to the tick data.
	 * <p>
	 * @return security descriptor
	 */
	public SecurityDescriptor getSecurityDescriptor();
	
	/**
	 * Get a date of the tick data segment.
	 * <p>
	 * @return date
	 */
	public LocalDate getDate();

}
