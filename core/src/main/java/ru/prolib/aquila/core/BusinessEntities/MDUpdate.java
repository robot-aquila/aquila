package ru.prolib.aquila.core.BusinessEntities;

import java.util.List;

/**
 * Market depth update record.
 */
public interface MDUpdate {

	public MDUpdateHeader getHeader();
	
	public List<MDUpdateRecord> getRecords();

}
