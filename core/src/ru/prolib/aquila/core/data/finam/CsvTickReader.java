package ru.prolib.aquila.core.data.finam;

import java.io.IOException;
import java.text.ParseException;
import org.joda.time.DateTime;

import ru.prolib.aquila.core.data.Finam;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.core.data.TickReader;

import com.csvreader.CsvReader;

/**
 * Ридер потока тиков на базе CSV-потока формата FINAM.
 * <p>
 * Использует идентификаторы полей, определенные в классе {@link Finam}. 
 */
public class CsvTickReader implements TickReader {
	private final CsvReader reader;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param reader ридер CSV-потока. Подразумевается, что заголовки CSV-файла
	 * уже прочитаны посредством вызова {@link CsvReader#readHeaders()}.
	 */
	public CsvTickReader(CsvReader reader) {
		super();
		this.reader = reader;
	}

	@Override
	public Tick read() throws IOException {
		if ( ! reader.readRecord() ) {
			return null;
		}
		try {
			return new Tick(new DateTime(Finam.df.parse(reader.get(Finam.DATE)
					+ " " + reader.get(Finam.TIME))),
				Double.parseDouble(reader.get(Finam.LAST)),
				Double.parseDouble(reader.get(Finam.VOLUME)));
		} catch ( ParseException e ) {
			throw new IOException(e);
		}
	}

	@Override
	public void close() {
		reader.close();
	}

}
