package ru.prolib.aquila.probe.timeline;

/**
 * Интерфейс источника событий.
 */
public interface TLEventSource {
	
	/**
	 * Получить очередное событие.
	 * <p>
	 * @return очередное событие или null, если нет больше событий
	 * @throws TLException 
	 */
	public TLEvent readNextEvent() throws TLException;
	
	/**
	 * Завершить работу с источником событий.
	 */
	public void close();

}
