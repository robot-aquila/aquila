package ru.prolib.aquila.core.data.finam;

import java.io.IOException;
import java.text.ParseException;
import ru.prolib.aquila.core.data.*;
import com.csvreader.CsvReader;

/**
 * Ридер потока тиков на базе CSV-потока формата FINAM.
 * <p>
 * Использует идентификаторы полей, определенные в классе {@link Finam}. 
 */
public class CsvTickReader implements Aqiterator<Tick> {
	private final CsvReader reader;
	private Tick curr;
	private boolean closed = false;
	
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
	public boolean next() throws DataException {
		try {
			if ( closed ) {
				return false;
			}
			if ( ! reader.readRecord() ) {
				close();
				return false;
			}
			curr = new Tick(
				Finam.parseDateTime(reader.get(Finam.DATE),
									reader.get(Finam.TIME)),
				Double.parseDouble(reader.get(Finam.LAST)),
				Double.parseDouble(reader.get(Finam.VOLUME)));
			return true;
		} catch ( ParseException e ) {
			throw new DataException(e);
		} catch ( IOException e ) {
			throw new DataException(e);	
		}
	}
	
	@Override
	public Tick item() throws DataException {
		if ( curr == null || closed ) {
			throw new DataException("No data under cursor");
		}
		return curr;
	}

	@Override
	public void close() {
		reader.close();
		closed = true;
	}

}
