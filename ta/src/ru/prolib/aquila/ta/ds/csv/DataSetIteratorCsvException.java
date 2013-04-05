package ru.prolib.aquila.ta.ds.csv;

import ru.prolib.aquila.ta.ds.DataSetException;

public class DataSetIteratorCsvException extends DataSetException {
	private static final long serialVersionUID = 1L;

	public DataSetIteratorCsvException(String msg) {
		super(msg);
	}
	
	public DataSetIteratorCsvException(String msg, Throwable t) {
		super(msg, t);
	}

}
