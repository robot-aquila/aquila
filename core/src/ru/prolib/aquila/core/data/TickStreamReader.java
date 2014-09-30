package ru.prolib.aquila.core.data;

import java.io.IOException;

/**
 * Интерфейс ридера потока тиковых данных.
 * <p>
 */
public interface TickStreamReader {
	
	/**
	 * Извлечь очередной тик данных.
	 * <p>
	 * @return тик данных или null, если достигнут конец потока
	 * @throws IOException
	 */
	public Tick read() throws IOException;
	
	/**
	 * Закрыть поток и освободить занятые ресурсы. 
	 */
	public void close();

}
