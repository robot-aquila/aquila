package ru.prolib.aquila.dde.utils.table;

import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.dde.DDETable;

/**
 * Набор рядов именованных объектов на основе DDE таблицы.
 * <p>
 * 2012-08-10<br>
 * $Id: DDETableRowSet.java 576 2013-03-14 12:07:25Z whirlwind $
 */
public class DDETableRowSet implements RowSet {
	private static int BEFORE_FIRST = -1;
	private static int AFTER_LAST = -2;
	private final Map<String, Integer> header;
	private final DDETable table;
	private int current = BEFORE_FIRST;
	
	public DDETableRowSet(DDETable table, Map<String, Integer> header) {
		super();
		this.table = table;
		this.header = header;
	}
	
	/**
	 * Получить таблицу.
	 * <p>
	 * @return таблица
	 */
	public DDETable getTable() {
		return table;
	}
	
	/**
	 * Получить карту заголовков.
	 * <p>
	 * @return карта
	 */
	public Map<String, Integer> getHeaders() {
		return header;
	}

	@Override
	public synchronized boolean next() {
		if ( current == AFTER_LAST ) {
			return false;
		}
		current ++;
		if ( current >= table.getRows() ) {
			current = AFTER_LAST;
			return false;
		}
		return true;
	}

	@Override
	public synchronized void reset() {
		current = BEFORE_FIRST;
	}

	@Override
	public synchronized Object get(String column) {
		if ( current == AFTER_LAST || current == BEFORE_FIRST ) {
			return null;
		}
		Integer index = header.get(column);
		return index == null ? null : table.getCell(current, index);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other instanceof DDETableRowSet ) {
			DDETableRowSet o = (DDETableRowSet) other;
			return new EqualsBuilder()
				.append(table, o.table)
				.append(header, o.header)
				.append(current, o.current)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121107, 144411)
			.append(table)
			.append(header)
			.append(current)
			.toHashCode();
	}

	@Override
	public void close() {
		reset();
	}

}
