package ru.prolib.aquila.probe;


public interface HistoricalDataReader<T> {
	
	public abstract T read()	throws HistoricalDataException ;
	
	public void close();

}
