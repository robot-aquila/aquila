package ru.prolib.aquila.core.timetable;

import org.joda.time.DateTime;

/**
 * Интерфейс временного периода.
 */
public interface Period {
	
	/**
	 * Проверить вхождение в период.
	 * <p>
	 * @param time временная метка
	 * @return true - время внутри периода, иначе - время за пределами периода  
	 */
	public boolean contains(DateTime time);

}
