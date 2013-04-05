package ru.prolib.aquila.core.data.row;



/**
 * Интерфейс обработчика ряда.
 * <p>
 * 2012-08-13<br>
 * $Id: DDERowHandler.java 255 2012-08-15 17:42:52Z whirlwind $
 */
public interface RowHandler {
	
	/**
	 * Обработать ряд.
	 * <p>
	 * @param row ряд
	 */
	public void handle(Row row);

}
