package ru.prolib.aquila.core;

import java.util.List;

/**
 * Служебный интерфейс типа события.
 */
public interface EventTypeSI extends EventType {

	/**
	 * Очистить списки получателей.
	 * <p>
	 */
	public void removeListeners();

	/**
	 * Получить идентификатор типа события.
	 * <p>
	 * Идентификатор позволяет отличать конкретный тип события среди множества
	 * других типов по уникальной строке. Идентификатор задается явно при
	 * создании объекта или назначается автоматически, если не указан.
	 * Предназначен для использования в отладочных целях.
	 * <p>
	 * @return строковый идентификатор
	 */
	public String getId();
	
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
	
	/**
	 * Проверить принадлежность к синхронным получателям.
	 * <p>
	 * @param listener получатель
	 * @return true - если это синхронный получатель событий, false - иначе
	 */
	public boolean isSyncListener(EventListener listener);
	
	/**
	 * Проверить принадлежность к асинхронным получателям.
	 * <p>
	 * @param listener получатель
	 * @return true - если это асинхронный получатель событий, false - иначе 
	 */
	public boolean isAsyncListener(EventListener listener);
	
	/**
	 * Проверить режим только синхронной трансляции.
	 * <p>
	 * @return true - если разрешена только синхронная трансляция событий 
	 */
	public boolean isOnlySyncMode();
	
}
