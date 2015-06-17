package ru.prolib.aquila.datatools.tickdatabase.simple;

import java.io.IOException;

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
			currentSegment = segmentManager.openWriter(descr, date);
		} else if ( ! date.equals(currentSegment.getDate()) ) {
			closeCurrentWriter();
			currentSegment = segmentManager.openWriter(descr, date);
		}
		currentSegment.write(tick);
	}
	
	private void closeCurrentWriter() throws GeneralException {
		try {
			segmentManager.close(currentSegment);
		} finally {
			currentSegment = null;
		}
	}

	@Override
	public void close() throws IOException {
		if ( currentSegment != null ) {
			try {
				closeCurrentWriter();
			} catch ( GeneralException e ) {
				throw new IOException(e);
			}
		}
	}

	@Override
	public SecurityDescriptor getSecurityDescriptor() {
		return descr;
	}
	
	public DataSegmentManager getDataSegmentManager() {
		return segmentManager;
	}

	@Override
	public void flush() throws IOException {
		if ( currentSegment != null ) {
			currentSegment.flush();
		}
	}

}
