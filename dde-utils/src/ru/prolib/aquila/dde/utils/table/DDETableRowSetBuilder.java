package ru.prolib.aquila.dde.utils.table;

import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.dde.DDETable;

/**
 * Интерфейс конструктора набора рядов на основе DDE-таблицы.
 * <p>
 * 2012-08-14<br>
 * $Id: DDETableRowSetBuilder.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public interface DDETableRowSetBuilder {
	
	/**
	 * Создать набор рядов.
	 * <p>
	 * На основе DDE-таблицы создает набор рядов объектов.
	 * <p>
	 * @param table таблица
	 * @return набор рядов
	 */
	public RowSet createRowSet(DDETable table);

}
