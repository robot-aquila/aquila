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
	public TLEvent pullEvent() throws TLException;
	
	/**
	 * Завершить работу с источником событий.
	 */
	public void close();
	
	/**
	 * Проверить факт закрытия источника.
	 * <p>
	 * @return true - источник закрыт, false - открыт
	 */
	public boolean closed();

}
