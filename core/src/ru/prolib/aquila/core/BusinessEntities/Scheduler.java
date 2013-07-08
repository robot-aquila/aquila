package ru.prolib.aquila.core.BusinessEntities;

import java.util.Date;

/**
 * Интерфейс планировщика задач.
 */
public interface Scheduler {
	
	/**
	 * Получить текущее время.
	 * <p>
	 * @return текущее время
	 */
	public Date getCurrentTime();

}
