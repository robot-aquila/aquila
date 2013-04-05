package ru.prolib.aquila.ta.ds;

import ru.prolib.aquila.core.data.Candle;


public interface BarWriter {
	
	public boolean addBar(Candle bar) throws BarWriterException;
	
	public boolean flush() throws BarWriterException;
	
}