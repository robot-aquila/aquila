package ru.prolib.aquila.datatools.tickdatabase.simple;


import java.io.IOException;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.GeneralException;
import ru.prolib.aquila.datatools.tickdatabase.TickWriter;

public class DataSegmentWriterImpl implements DataSegmentWriter {
	private final LocalDate date;
	private final TickWriter writer;
	private final SecurityDescriptor descr;
	private int lastNumber;
	private LocalTime lastTime;
	
	public DataSegmentWriterImpl(SecurityDescriptor descr, LocalDate date,
			TickWriter writer, LocalTime lastTime, int lastTickNumber)
	{
		super();
		this.descr = descr;
		this.date = date;
		this.writer = writer;
		this.lastTime = lastTime;
		this.lastNumber = lastTickNumber;
	}
	
	public DataSegmentWriterImpl(SecurityDescriptor descr, LocalDate date,
			TickWriter writer)
	{
		this(descr, date, writer, LocalTime.MIDNIGHT, 0);
	}

	@Override
	public LocalDate getDate() {
		return date;
	}
	

	@Override
	public SecurityDescriptor getSecurityDescriptor() {
		return descr;
	}
	
	/**
	 * Get underlying writer.
	 * <p>
	 * @return the writer
	 */
	public TickWriter getWriter() {
		return writer;
	}
	
	/**
	 * Get number of last tick.
	 * <p>
	 * @return the number of last tick
	 */
	public int getNumberOfLastTick() {
		return lastNumber;
	}
	
	/**
	 * Get time of last tick.
	 * <p>
	 * @return the time of last tick
	 */
	public LocalTime getTimeOfLastTick() {
		return lastTime;
	}
	
	@Override
	public void write(Tick tick) throws GeneralException {
		LocalDate d = tick.getTime().toLocalDate();
		if ( ! date.equals(d) ) {
			throw new GeneralException("Tick date mismatch: tick="
					+ d + " segment=" + date);
		}
		writer.write(tick);
		LocalTime t = tick.getTime().toLocalTime();
		if ( t.compareTo(lastTime) < 0 ) {
			throw new GeneralException("Cannot write the past data: tick="
					+ t + " segment=" + lastTime);
		}
		lastTime = t;
		lastNumber ++;
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}

}
