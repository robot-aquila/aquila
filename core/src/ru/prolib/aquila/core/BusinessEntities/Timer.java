package ru.prolib.aquila.core.BusinessEntities;

import java.util.Date;

/**
 * Интерфейс таймера.
 */
public interface Timer {
	
	/**
	 * Получить текущее время.
	 * <p>
	 * @return текущее время
	 */
	public Date getCurrentTime();

}
