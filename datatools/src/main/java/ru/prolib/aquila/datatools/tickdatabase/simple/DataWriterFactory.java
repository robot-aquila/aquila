package ru.prolib.aquila.datatools.tickdatabase.simple;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.datatools.GeneralException;

public interface DataWriterFactory {
	
	public DataWriter createWriter(SecurityDescriptor descr)
			throws GeneralException;

}
