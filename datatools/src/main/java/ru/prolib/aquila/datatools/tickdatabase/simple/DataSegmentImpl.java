package ru.prolib.aquila.datatools.tickdatabase.simple;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.datatools.tickdatabase.TickWriter;

public class DataSegmentImpl implements DataSegment {
	private final LocalDate date;
	private final TickWriter writer;
	private final Symbol symbol;
	private int lastNumber;
	private LocalTime lastTime;
	
	public DataSegmentImpl(Symbol symbol, LocalDate date,
			TickWriter writer, LocalTime lastTime, int lastTickNumber)
	{
		super();
		this.symbol = symbol;
		this.date = date;
		this.writer = writer;
		this.lastTime = lastTime;
		this.lastNumber = lastTickNumber;
	}
	
	public DataSegmentImpl(Symbol symbol, LocalDate date,
			TickWriter writer)
	{
		this(symbol, date, writer, LocalTime.MIDNIGHT, 0);
	}

	@Override
	public LocalDate getDate() {
		return date;
	}
	

	@Override
	public Symbol getSymbol() {
		return symbol;
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
	public void write(Tick tick) throws IOException {
		LocalDateTime dt = LocalDateTime.ofInstant(tick.getTime(), ZoneOffset.UTC);
		LocalDate d = dt.toLocalDate();
		if ( ! date.equals(d) ) {
			throw new IOException("Tick date mismatch: tick="
					+ d + " segment=" + date);
		}
		writer.write(tick);
		LocalTime t = dt.toLocalTime();
		if ( t.compareTo(lastTime) < 0 ) {
			throw new IOException("Cannot write the past data: tick="
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
