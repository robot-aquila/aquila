package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.List;

/**
 * Market depth update record.
 */
public interface MDUpdate extends TStamped, MDUpdateHeader {

	public MDUpdateHeader getHeader();
	
	public List<MDUpdateRecord> getRecords();
	
	/**
	 * Create a new update with the new time of header and all records.
	 * <p>
	 * @param newTime - the new time
	 * @return a new update instance
	 */
	public MDUpdate withTime(Instant newTime);

}
