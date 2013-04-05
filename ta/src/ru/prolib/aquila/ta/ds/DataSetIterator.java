package ru.prolib.aquila.ta.ds;

public interface DataSetIterator extends DataSet {
	
	public boolean next() throws DataSetException;
	
	public void close();

}
