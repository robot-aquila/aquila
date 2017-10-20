package ru.prolib.aquila.dde.utils.table;

import ru.prolib.aquila.dde.DDEException;
import ru.prolib.aquila.dde.DDETable;

/**
 * Интерфейс обработчика DDE-таблицы.
 * <p>
 * 2012-08-10<br>
 * $Id: DDETableHandler.java 255 2012-08-15 17:42:52Z whirlwind $
 */
public interface DDETableHandler {
	
	/**
	 * Обработать таблицу.
	 * <p>
	 * @param table таблица
	 */
	public void handle(DDETable table) throws DDEException;

}
