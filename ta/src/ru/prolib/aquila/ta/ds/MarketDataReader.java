package ru.prolib.aquila.ta.ds;

public interface MarketDataReader {

	/**
	 * Прочитать подготовительную порцию информации.
	 * 
	 * @return
	 * @throws MarketDataException
	 */
	public DataSetIterator prepare() throws MarketDataException;
	
	/**
	 * Прочитать следующую порцию информации.
	 * 
	 * @return
	 * @throws MarketDataException
	 */
	public DataSetIterator update() throws MarketDataException;

}
