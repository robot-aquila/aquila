package ru.prolib.aquila.datatools.finam;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.csvreader.CsvWriter;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.tickdatabase.TickWriter;

/**
 * Tick-data writer.
 * <p>
 * Writes tick-data to CSV-file. The output is close to Finam CSV file
 * of tick data and "DATE, TIME, LAST, VOL" format. The DATA column is not using
 * because the date can be determined by file name. Additional column
 * MILLISECONDS stores a millisecond of tick time. 
 */
public class CsvTickWriter implements TickWriter {
	private static final DateTimeFormatter timeFormat;
	
	static {
		timeFormat = DateTimeFormat.forPattern("HHmmss");
	}
	
	private final CsvWriter writer;
	private final Security security;
	
	public CsvTickWriter(Security security, OutputStream stream) {
		super();
		this.security = security;
		writer = new CsvWriter(stream, ',', Charset.forName("UTF-8"));
	}

	@Override
	public void write(Tick tick) throws IOException {
		try {
			String entries[] = {
				timeFormat.print(tick.getTime()),
				security.shrinkPrice(tick.getValue()),
				Long.toString(tick.getOptionalValueAsLong()),
				Long.toString(tick.getTime().getMillisOfSecond())
			};
			writer.writeRecord(entries);
		} catch ( IOException e ) {
			throw e;
		} catch ( Exception e ) {
			throw new IOException(e);
		}
	}
	
	public void writeHeader() throws IOException {
		String headers[] = { "<TIME>", "<LAST>", "<VOL>", "<MILLISECONDS>" };
		writer.writeRecord(headers);
	}

	@Override
	public void close() {
		writer.close();
	}

	@Override
	public void flush() {
		writer.flush();
	}

}
