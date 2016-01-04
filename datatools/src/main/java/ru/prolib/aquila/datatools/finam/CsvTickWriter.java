package ru.prolib.aquila.datatools.finam;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.csvreader.CsvWriter;

import ru.prolib.aquila.core.BusinessEntities.Tick;
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
		timeFormat = DateTimeFormatter.ofPattern("HHmmss");
	}
	
	private final CsvWriter writer;
	
	public CsvTickWriter(OutputStream stream) {
		super();
		writer = new CsvWriter(stream, ',', Charset.forName("UTF-8"));
	}

	@Override
	public void write(Tick tick) throws IOException {
		try {
			String entries[] = {
				timeFormat.format(LocalDateTime.ofInstant(tick.getTime(), ZoneOffset.UTC)),
				formatPrice(tick.getPrice()),
				Long.toString(tick.getSize()),
				Long.toString(tick.getTime().getNano() / 1000000)
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
	
	private String formatPrice(Double value) {
		return value == null ? "0" : value.toString();
	}

}
