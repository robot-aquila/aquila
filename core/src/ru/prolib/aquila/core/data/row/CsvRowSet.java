package ru.prolib.aquila.core.data.row;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.csvreader.CsvReader;

/**
 * Набор рядов на основе CSV файла.
 * <p>
 * 2013-03-03<br>
 * $Id: CsvRowSet.java 563 2013-03-08 20:02:34Z whirlwind $
 */
public class CsvRowSet implements RowSet {
	private final File file;
	private CsvReader reader;
	
	public CsvRowSet(File file) throws FileNotFoundException {
		super();
		if ( ! file.exists() ) {
			throw new FileNotFoundException(file.getAbsolutePath());
		}
		this.file = file;
	}
	
	/**
	 * Получить CSV-файл.
	 * <p>
	 * @return файл
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * Установить устройство чтения записей.
	 * <p>
	 * Только для тестов.
	 * <p>
	 * @param reader устройство чтения записей
	 */
	protected synchronized void setCsvReader(CsvReader reader) {
		this.reader = reader;
	}

	@Override
	public synchronized Object get(String name) throws RowException {
		if ( reader == null ) {
			throw new RowSetException("Not positioned: " + file);
		}
		try {
			return reader.get(name);
		} catch ( IOException e ) {
			throw new RowSetException("Error reading CSV: ", e);
		}
	}

	@Override
	public synchronized boolean next() throws RowSetException {
		if ( reader == null ) {
			try {
				reader = new CsvReader(file.getAbsolutePath());
				reader.readHeaders();
			} catch ( IOException e ) {
				reset();
				throw new RowSetException("Error opening file", e);
			}

		}
		try {
			return reader.readRecord();
		} catch ( IOException e ) {
			reset();
			throw new RowSetException("Error reading record", e);
		}
	}

	@Override
	public synchronized void reset() {
		if ( reader != null ) {
			reader.close();
			reader = null;
		}
	}

	@Override
	public synchronized void close() {
		reset();
	}

	@Override
	public synchronized Row getRowCopy() throws RowException {
		if ( reader == null ) {
			throw new RowSetException("Not positioned: " + file);
		}
		try {
			return new SimpleRow(reader.getHeaders(), reader.getValues());
		} catch ( IOException e ) {
			reset();
			throw new RowSetException(e);
		}
	}

}
