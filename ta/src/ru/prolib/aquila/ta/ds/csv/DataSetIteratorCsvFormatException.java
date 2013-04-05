package ru.prolib.aquila.ta.ds.csv;

public class DataSetIteratorCsvFormatException extends
		DataSetIteratorCsvException
{
	private static final long serialVersionUID = 1L;

	public DataSetIteratorCsvFormatException(String column,
											 NumberFormatException e)
	{
		super("Column " + column + " format error: " + e.getMessage(), e);
	}

}
