package ru.prolib.aquila.core.data;

/**
 * Интерфейс фабрики потока тиковых данных.
 */
public interface TickReaderFactory {
	
	/**
	 * Создать поток тиковых данных для чтения.
	 * <p>
	 * @param param параметр создания
	 * @return поток тиковых данных
	 * @throws DataException - If error occurred.
	 */
	public Aqiterator<Tick> createTickReader(String param) throws DataException;

}
