package ru.prolib.aquila.datatools.tickdatabase.simple;

import org.joda.time.LocalDate;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.GeneralException;

public class DataWriterImpl implements DataWriter {
	private final SecurityDescriptor descr;
	private final DataSegmentManager segmentManager;
	private DataSegmentWriter currentSegment;
	
	public DataWriterImpl(SecurityDescriptor descr,
			DataSegmentManager segmentManager)
	{
		super();
		this.descr = descr;
		this.segmentManager = segmentManager;
	}

	@Override
	public void write(Tick tick) throws GeneralException {
		LocalDate date = tick.getTime().toLocalDate();
		if ( currentSegment == null ) {
			currentSegment = segmentManager.open(descr, date);
		} else if ( ! date.equals(currentSegment.getDate()) ) {
			segmentManager.close(currentSegment);
			currentSegment = segmentManager.open(descr, date);
		}
		currentSegment.write(tick);
	}

	@Override
	public void close() {
		if ( currentSegment != null ) {
			currentSegment.close();
			currentSegment = null;
		}
	}

	@Override
	public SecurityDescriptor getSecurityDescriptor() {
		return descr;
	}
	
	public DataSegmentManager getDataSegmentManager() {
		return segmentManager;
	}

}
