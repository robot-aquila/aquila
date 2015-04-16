package ru.prolib.aquila.core;

import java.util.List;

/**
 * Служебный интерфейс типа события.
 */
public interface EventTypeSI extends EventType {

	/**
	 * Очистить списки получателей.
	 */
	public void removeListeners();

	/**
	 * Получить общее количество получателей.
	 * <p>
	 * @return общее количество получателей
	 */
	public int countListeners();
	
	/**
	 * Получить список асинхронных получателей.
	 * <p>
	 * @return дубликат списка асинхронных получателей
	 */
	public List<EventListener> getAsyncListeners();
	
	/**
	 * Получить список синхронных получателей.
	 * <p>
	 * @return дубликат списка синхронных получателей
	 */
	public List<EventListener> getSyncListeners();
	

}
