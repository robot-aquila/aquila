package ru.prolib.aquila.core;


/**
 * Интерфейс обработчика событий.
 * <p>
 * 2012-04-09<br>
 * $Id: EventListener.java 222 2012-06-22 07:44:18Z whirlwind $
 */
public interface EventListener {
	
	/**
	 * Обработать событие
	 * @param event событие
	 */
	public void onEvent(Event event);

}
