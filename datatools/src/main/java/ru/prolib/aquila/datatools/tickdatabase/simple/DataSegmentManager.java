package ru.prolib.aquila.datatools.tickdatabase.simple;

import org.joda.time.LocalDate;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.GeneralException;

/**
 * Data segment manager interface.
 */
public interface DataSegmentManager {

	/**
	 * Open a tick data writer.
	 * <p> 
	 * @param descr - associated security descriptor
	 * @param date - associated date
	 * @return data writer
	 * @throws GeneralException - error opening the data segment for writing
	 */
	public DataSegmentWriter
		openWriter(SecurityDescriptor descr, LocalDate date)
			throws GeneralException;
	
	/**
	 * Close the previously opened data writer.
	 * <p>
	 * @param writer - the tick data writer
	 * @throws GeneralException - error closing the data segment
	 */
	public void close(DataSegmentWriter writer) throws GeneralException;
	
	/**
	 * Open a tick data reader.
	 * <p> 
	 * @param descr - associated security descriptor
	 * @param date - associated date
	 * @return data reader
	 * @throws GeneralException - error opening data segment for reading
	 */
	public Aqiterator<Tick>
		openReader(SecurityDescriptor descr, LocalDate date)
			throws GeneralException;
	
	/**
	 * Close the previously opened data reader. 
	 * <p>
	 * @param reader - data reader
	 * @throws GeneralException - error closing data segment
	 */
	public void close(Aqiterator<Tick> reader) throws GeneralException;
	
	/**
	 * Test the data availability.
	 * <p>
	 * @param descr - security descriptor
	 * @return - true - if at least one data segment exists for the specified
	 * security 
	 * @throws GeneralException - error accessing storage
	 */
	public boolean isDataAvailable(SecurityDescriptor descr)
		throws GeneralException;
	
	/**
	 * Test the data segment availability.
	 * <p>
	 * @param descr - security descriptor
	 * @param date - date of the data segment
	 * @return true - if the segment exists, false - if not exists
	 * @throws GeneralException - error accessing storage
	 */
	public boolean isDataAvailable(SecurityDescriptor descr, LocalDate date)
		throws GeneralException;
	
	/**
	 * Get date of the first available segment.
	 * <p>
	 * @param descr - security descriptor
	 * @return date of the first available segment
	 * @throws GeneralException - error accessing storage or data not available 
	 */
	public LocalDate getFirstSegment(SecurityDescriptor descr)
		throws GeneralException;
	
	/**
	 * Get date of the last available segment.
	 * <p>
	 * @param descr - security descriptor
	 * @return date of the last available segment
	 * @throws GeneralException - error accessing storage or data not available
	 */
	public LocalDate getLastSegment(SecurityDescriptor descr)
		throws GeneralException;

}
