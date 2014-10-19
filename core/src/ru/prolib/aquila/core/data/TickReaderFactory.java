package ru.prolib.aquila.core.data;

/**
 * Интерфейс фабрики потока тиковых данных.
 * <p>
 */
public interface TickReaderFactory {
	
	/**
	 * Создать поток тиковых данных для чтения.
	 * <p>
	 * @param param параметр создания
	 * @return поток тиковых данных
	 * @throws DataException
	 */
	public TickReader createTickReader(String param) throws DataException;

}
