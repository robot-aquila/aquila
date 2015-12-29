package ru.prolib.aquila.datatools.finam;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import com.csvreader.CsvReader;

import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.DataException;
import ru.prolib.aquila.core.data.Tick;

/**
 * Tick data segment reader.
 * <p>
 * Reads tick data from CSV file.
 */
public class CsvTickReader implements Aqiterator<Tick> {
	private static final String LAST = "<LAST>";
	private static final String VOL = "<VOL>";
	private static final String TIME = "<TIME>";
	private static final String MILLISECONDS = "<MILLISECONDS>";
	private static final DateTimeFormatter timeFormat;
	
	static {
		timeFormat = DateTimeFormatter.ofPattern("HHmmss");
	}
	
	private final CsvReader csvReader;
	private final LocalDate date;
	private Tick curr;
	private boolean closed = false;
	private boolean hasMilliseconds = false;
	
	public CsvTickReader(CsvReader csvReader, LocalDate date) {
		super();
		this.csvReader = csvReader;
		this.date = date;
	}
	
	public CsvTickReader(InputStream inputStream, LocalDate date) {
		this(new CsvReader(new InputStreamReader(inputStream)), date);
	}
	
	public CsvReader getCsvReader() {
		return csvReader;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public boolean isClosed() {
		return closed;
	}
	
	public boolean hasMilliseconds() {
		return hasMilliseconds;
	}
	
	public void readHeader() throws IOException {
		if ( csvReader.readHeaders() ) {
			String headers[] = csvReader.getHeaders();
			for ( String x : headers ) {
				if ( x.equals(MILLISECONDS) ) {
					hasMilliseconds = true;
					break;
				}
			}
		}
	}

	@Override
	public void close() {
		csvReader.close();
		closed = true;
	}

	@Override
	public Tick item() throws DataException {
		if ( curr == null || closed ) {
			throw new DataException("No data under cursor");
		}
		return curr;
	}

	@Override
	public boolean next() throws DataException {
		if ( closed ) {
			return false;
		}
		try {
			if ( ! csvReader.readRecord() ) {
				close();
				return false;
			}
			LocalDateTime dt = date.atTime(LocalTime.parse(csvReader.get(TIME), timeFormat));
			if ( hasMilliseconds ) {
				dt = dt.plus(Long.parseLong(csvReader.get(MILLISECONDS)), ChronoUnit.MILLIS);
			}
			curr = new Tick(dt, Double.parseDouble(csvReader.get(LAST)),
					Double.parseDouble(csvReader.get(VOL)));
			return true;
		} catch ( Exception e ) {
			throw new DataException(e);
		}
	}

}
