package ru.prolib.aquila.core;

/**
 * Интерфейс события.
 * <p>
 * 2012-04-13<br>
 * $Id: Event.java 219 2012-05-20 12:16:45Z whirlwind $
 */
public interface Event {

	/**
	 * Получить тип события
	 * <p>
	 * @return тип события
	 */
	public EventType getType();

	/**
	 * Проверить соответствие типу
	 * <p>
	 * @param type тип события
	 * @return true данное событие соответствующего типа, иначе - false
	 */
	public boolean isType(EventType type);

}