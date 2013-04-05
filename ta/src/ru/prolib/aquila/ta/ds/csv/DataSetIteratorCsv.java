package ru.prolib.aquila.ta.ds.csv;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;

import ru.prolib.aquila.ta.ds.DataSetException;
import ru.prolib.aquila.ta.ds.DataSetIterator;

import com.csvreader.CsvReader;

public class DataSetIteratorCsv implements DataSetIterator {
	private final CsvReader csv;
	private final HashSet<String> columns;
	
	public DataSetIteratorCsv(CsvReader csvReader)
		throws DataSetIteratorCsvIoException
	{
		csv = csvReader;
		columns = new HashSet<String>();
		cacheHeaders();
	}
	
	private void cacheHeaders() throws DataSetIteratorCsvIoException {
		try {
			String headers[] = csv.getHeaders();
			for ( int i = 0; i < headers.length; i ++ ) {
				columns.add(headers[i]);
			}
		} catch ( IOException e ) {
			throw new DataSetIteratorCsvIoException(e);
		}
	}
	
	public CsvReader getCsvReader() {
		return csv;
	}
	
	@Override
	public String getString(String name) throws DataSetException {
		if ( ! columns.contains(name) ) {
			throw new DataSetIteratorCsvColumnNotExistsException(name);
		}
		try {
			return csv.get(name);
		} catch ( IOException e ) {
			throw new DataSetIteratorCsvIoException(e);
		}
	}

	@Override
	public Double getDouble(String name) throws DataSetException {
		try {
			return Double.parseDouble(getString(name));
		} catch ( NumberFormatException e ) {
			throw new DataSetIteratorCsvFormatException(name, e);
		}
	}

	@Override
	public Long getLong(String name) throws DataSetException {
		try {
			return Long.parseLong(getString(name));
		} catch ( NumberFormatException e ) {
			throw new DataSetIteratorCsvFormatException(name, e);
		}
	}
	
	@Override
	public Date getDate(String name) throws DataSetException {
		throw new DataSetIteratorCsvUnsupportedException("getDate");
	}

	@Override
	public boolean next() throws DataSetException {
		try {
			return csv.readRecord();
		} catch ( IOException e ) {
			throw new DataSetIteratorCsvIoException(e);
		}
	}

	@Override
	public void close() {
		csv.close();
	}

}
