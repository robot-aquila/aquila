package ru.prolib.aquila.ta.ds;

import java.util.Date;

public class DataSetIteratorEmpty implements DataSetIterator {
	
	public DataSetIteratorEmpty() {
		super();
	}

	@Override
	public Double getDouble(String name) throws DataSetException {
		throw new DataSetException("Not available");
	}

	@Override
	public String getString(String name) throws DataSetException {
		throw new DataSetException("Not available");
	}

	@Override
	public Date getDate(String name) throws DataSetException {
		throw new DataSetException("Not available");
	}

	@Override
	public Long getLong(String name) throws DataSetException {
		throw new DataSetException("Not available");
	}

	@Override
	public boolean next() throws DataSetException {
		return false;
	}

	@Override
	public void close() {

	}

}
