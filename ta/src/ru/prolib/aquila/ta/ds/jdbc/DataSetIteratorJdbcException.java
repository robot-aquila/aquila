package ru.prolib.aquila.ta.ds.jdbc;

import ru.prolib.aquila.ta.ds.DataSetException;

public class DataSetIteratorJdbcException extends DataSetException {
	private static final long serialVersionUID = 1L;

	public DataSetIteratorJdbcException(Exception e) {
		super(e.getMessage(), e);
	}

}
