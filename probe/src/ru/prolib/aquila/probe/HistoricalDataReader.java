package ru.prolib.aquila.probe;

public interface HistoricalDataReader<T> {
	
	public T read() throws HistoricalDataException;
	
	public void close();

}
