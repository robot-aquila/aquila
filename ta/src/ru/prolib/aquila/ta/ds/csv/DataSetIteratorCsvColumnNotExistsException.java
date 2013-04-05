package ru.prolib.aquila.ta.ds.csv;

public class DataSetIteratorCsvColumnNotExistsException extends
		DataSetIteratorCsvException
{
	private static final long serialVersionUID = 1L;

	public DataSetIteratorCsvColumnNotExistsException(String column) {
		super("Column not exists: " + column);
	}

}
