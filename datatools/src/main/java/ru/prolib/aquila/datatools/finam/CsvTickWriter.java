package ru.prolib.aquila.datatools.finam;

import java.io.*;
import java.nio.charset.Charset;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.csvreader.CsvWriter;

import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.Trade;

/**
 * Tick-data writer.
 * <p>
 * Writes tick-data to CSV-file. The output is compatible with Finam CSV file
 * for tick data and "DATE, TIME, LAST, VOL" format.  
 */
public class CsvTickWriter implements Closeable {
	private static final DateTimeFormatter dateFormat;
	private static final DateTimeFormatter timeFormat;
	
	static {
		dateFormat = DateTimeFormat.forPattern("yyyyMMdd");
		timeFormat = DateTimeFormat.forPattern("HHmmss");
	}
	
	private final CsvWriter writer;
	
	public CsvTickWriter(OutputStream stream) {
		super();
		writer = new CsvWriter(stream, ',', Charset.forName("UTF-8"));
	}
	
	public void write(Trade trade) throws IOException {
		try {
			String entries[] = {
				dateFormat.print(trade.getTime()),
				timeFormat.print(trade.getTime()),
				trade.getSecurity().shrinkPrice(trade.getPrice()),
				Long.toString(trade.getQty())
			};
			writer.writeRecord(entries);
		} catch ( SecurityException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public void writeHeader() throws IOException {
		String headers[] = { "<DATE>", "<TIME>", "<LAST>", "<VOL>" };
		writer.writeRecord(headers);
	}

	public void close() throws IOException {
		writer.close();
	}

}
