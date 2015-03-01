package ru.prolib.aquila.core;

/**
 * Сервисный интерфейс события.
 */
public interface EventSI extends Event {
	
	/**
	 * Получить сервисный интерфейс типа события.
	 * <p>
	 * @return тип события
	 */
	public EventTypeSI getTypeSI();
	
}
