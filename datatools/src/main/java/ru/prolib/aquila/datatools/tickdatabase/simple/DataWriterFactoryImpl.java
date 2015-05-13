package ru.prolib.aquila.datatools.tickdatabase.simple;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.datatools.GeneralException;

public class DataWriterFactoryImpl implements DataWriterFactory {
	private final DataSegmentManager segmentManager;
	
	public DataWriterFactoryImpl(DataSegmentManager segmentManager) {
		super();
		this.segmentManager = segmentManager;
	}

	@Override
	public DataWriter createWriter(SecurityDescriptor descr)
			throws GeneralException
	{
		return new DataWriterImpl(descr, segmentManager);
	}

}
