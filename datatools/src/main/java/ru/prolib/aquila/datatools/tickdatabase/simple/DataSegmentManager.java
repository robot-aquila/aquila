package ru.prolib.aquila.datatools.tickdatabase.simple;

import org.joda.time.LocalDate;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.datatools.GeneralException;

public interface DataSegmentManager {

	/**
	 * Get data writer for the security and date.
	 * <p> 
	 * @param descr - associated security descriptor
	 * @param date - associated date
	 * @return data writer
	 * @throws GeneralException - error opening the data segment for writing
	 */
	public DataSegmentWriter open(SecurityDescriptor descr, LocalDate date)
			throws GeneralException;
	
	/**
	 * Close the previously opened data writer.
	 * <p>
	 * @param writer - the tick data writer
	 * @throws GeneralException - error closing the data segment
	 */
	public void close(DataSegmentWriter writer) throws GeneralException;

}
