package ru.prolib.aquila.core.BusinessEntities;

import java.util.List;

/**
 * Market depth update record.
 */
public interface MDUpdate extends Timestamped {

	public MDUpdateHeader getHeader();
	
	public List<MDUpdateRecord> getRecords();

}
