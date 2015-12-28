package ru.prolib.aquila.core.timetable;

import java.time.LocalDateTime;

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
	public boolean contains(LocalDateTime time);

}
