package ru.prolib.aquila.ta.ds;

import java.util.Date;


public class DataSetIteratorDecorator implements DataSetIterator {
	private DataSetIterator iterator;

	public DataSetIteratorDecorator(DataSetIterator iterator) {
		super();
		this.iterator = iterator; 
	}
	
	public DataSetIteratorDecorator() {
		super();
		iterator = null;
	}

	@Override
	public Double getDouble(String name) throws DataSetException {
		return iterator.getDouble(name);
	}

	@Override
	public String getString(String name) throws DataSetException {
		return iterator.getString(name);
	}

	@Override
	public Date getDate(String name) throws DataSetException {
		return iterator.getDate(name);
	}

	@Override
	public boolean next() throws DataSetException {
		return iterator.next();
	}

	public DataSetIterator getDataSetIterator() {
		return iterator;
	}

	public void setDataSetIterator(DataSetIterator iterator) {
		this.iterator = iterator;
	}

	@Override
	public Long getLong(String name) throws DataSetException {
		return iterator.getLong(name);
	}

	@Override
	public void close() {
		iterator.close();
	}

}
