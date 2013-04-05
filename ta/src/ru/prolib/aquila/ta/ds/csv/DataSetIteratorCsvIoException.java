package ru.prolib.aquila.ta.ds.csv;

import java.io.IOException;

public class DataSetIteratorCsvIoException extends DataSetIteratorCsvException {
	private static final long serialVersionUID = 1L;

	public DataSetIteratorCsvIoException(IOException t) {
		super(t.getMessage(), t);
	}

}
