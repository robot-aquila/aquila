package ru.prolib.aquila.datatools.tickdatabase.simple;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.datatools.tickdatabase.TickWriter;

public interface DataWriter extends TickWriter {
	
	/**
	 * Get security descriptor related to the tick data.
	 * <p>
	 * @return security descriptor
	 */
	public SecurityDescriptor getSecurityDescriptor();

}
