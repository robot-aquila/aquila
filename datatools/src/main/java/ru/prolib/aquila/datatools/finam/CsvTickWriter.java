package ru.prolib.aquila.datatools.finam;

import java.io.OutputStream;
import java.nio.charset.Charset;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.csvreader.CsvWriter;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.GeneralException;
import ru.prolib.aquila.datatools.IOException;
import ru.prolib.aquila.datatools.tickdatabase.TickWriter;

/**
 * Tick-data writer.
 * <p>
 * Writes tick-data to CSV-file. The output is compatible with Finam CSV file
 * for tick data and "DATE, TIME, LAST, VOL" format.  
 */
public class CsvTickWriter implements TickWriter {
	private static final DateTimeFormatter dateFormat;
	private static final DateTimeFormatter timeFormat;
	
	static {
		dateFormat = DateTimeFormat.forPattern("yyyyMMdd");
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
	public void write(Tick tick) throws GeneralException {
		try {
			String entries[] = {
				dateFormat.print(tick.getTime()),
				timeFormat.print(tick.getTime()),
				security.shrinkPrice(tick.getValue()),
				Long.toString(tick.getOptionalValueAsLong())
			};
			writer.writeRecord(entries);
		} catch ( java.io.IOException e ) {
			throw new IOException(e);
		} catch ( Exception e ) {
			throw new GeneralException(e);
		}
	}
	
	public void writeHeader() throws GeneralException {
		String headers[] = { "<DATE>", "<TIME>", "<LAST>", "<VOL>" };
		try {
			writer.writeRecord(headers);
		} catch ( java.io.IOException e ) {
			throw new IOException(e);
		}
	}

	@Override
	public void close() {
		writer.close();
	}

}
