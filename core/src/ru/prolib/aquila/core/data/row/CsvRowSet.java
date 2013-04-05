package ru.prolib.aquila.core.data.row;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;

/**
 * Набор рядов на основе CSV файла.
 * <p>
 * 2013-03-03<br>
 * $Id: CsvRowSet.java 563 2013-03-08 20:02:34Z whirlwind $
 */
public class CsvRowSet implements RowSet {
	private static final Logger logger;
	private final File file;
	private CsvReader reader;
	
	static {
		logger = LoggerFactory.getLogger(CsvRowSet.class);
	}
	
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

	@Override
	public synchronized Object get(String name) {
		if ( reader == null ) {
			throw new IllegalStateException("Not positioned: " + file);
		}
		try {
			return reader.get(name);
		} catch ( IOException e ) {
			logger.error("Error reading CSV: ", e);
			return null;
		}
	}

	@Override
	public synchronized boolean next() {
		if ( reader == null ) {
			try {
				reader = new CsvReader(file.getAbsolutePath());
				reader.readHeaders();
			} catch ( IOException e ) {
				logger.error("Error open CSV: ", e);
				return false;
			}

		}
		try {
			return reader.readRecord();
		} catch ( IOException e ) {
			logger.error("Error reading CSV (close stream): ", e);
			reader = null;
			return false;
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

}
