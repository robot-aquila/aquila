package ru.prolib.aquila.datatools.tickdatabase.simple;

import java.time.LocalDate;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.datatools.tickdatabase.TickWriter;

/**
 * Interface of the tick data segment.
 * <p>
 * The tick data segment is an ordered set of ticks of security.
 * Each segment contains data of one day.
 */
public interface DataSegment extends TickWriter {
	
	/**
	 * Get symbol related to the tick data.
	 * <p>
	 * @return symbol info
	 */
	public Symbol getSymbol();
	
	/**
	 * Get a date of the tick data segment.
	 * <p>
	 * @return date
	 */
	public LocalDate getDate();

}
