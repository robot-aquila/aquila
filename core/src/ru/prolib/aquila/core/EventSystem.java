package ru.prolib.aquila.core;

/**
 * Интерфейс фабрики системы событий. 
 * <p>
 * 2012-04-21<br>
 * $Id: EventSystem.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public interface EventSystem {
	
	/**
	 * Получить используемую очередь событий.
	 * <p>
	 * @return очередь событий
	 */
	public EventQueue getEventQueue();
	
	/**
	 * Создать диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher createEventDispatcher();
	
	/**
	 * Создать диспетчер событий.
	 * <p>
	 * @param id идентификатор диспетчера
	 * @return диспетчер событий
	 */
	public EventDispatcher createEventDispatcher(String id);

}
