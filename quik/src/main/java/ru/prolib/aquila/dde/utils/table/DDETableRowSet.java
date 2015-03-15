package ru.prolib.aquila.dde.utils.table;

import java.util.*;
import org.apache.commons.lang3.builder.*;
import ru.prolib.aquila.core.data.row.*;
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
	private final int firstRowOffset;
	private int current = BEFORE_FIRST;
	
	public DDETableRowSet(DDETable table, Map<String, Integer> header) {
		this(table, header, 0);
	}
	
	public DDETableRowSet(DDETable table, Map<String, Integer> header,
			int firstRowOffset)
	{
		super();
		this.table = table;
		this.header = header;
		this.firstRowOffset = firstRowOffset;
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
	
	/**
	 * Получить смещение первого ряда (сколько рядов с начала пропустить).
	 * <p>
	 * @return смещение первого ряда
	 */
	public int getFirstRowOffset() {
		return firstRowOffset;
	}

	@Override
	public synchronized boolean next() {
		if ( current == AFTER_LAST ) {
			return false;
		}
		if ( current == BEFORE_FIRST ) {
			current = firstRowOffset;
		} else {
			current ++;
		}
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
	public synchronized Object get(String column) throws RowException {
		if ( current == AFTER_LAST || current == BEFORE_FIRST ) {
			throw new RowSetException("Not positioned");
		}
		Integer index = header.get(column);
		if ( index == null || index >= table.getCols() ) {
			return null;
		}
		return table.getCell(current, index);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == DDETableRowSet.class ) {
			DDETableRowSet o = (DDETableRowSet) other;
			return new EqualsBuilder()
				.append(table, o.table)
				.append(header, o.header)
				.append(current, o.current)
				.append(firstRowOffset, o.firstRowOffset)
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
			.append(firstRowOffset)
			.toHashCode();
	}

	@Override
	public synchronized void close() {
		reset();
	}

	@Override
	public synchronized Row getRowCopy() throws RowException {
		if ( current == AFTER_LAST || current == BEFORE_FIRST ) {
			throw new RowSetException("Not positioned");
		}
		Map<String, Object> map = new Hashtable<String, Object>();
		for ( String hdr : header.keySet() ) {
			Object value = get(hdr);
			if ( value != null ) {
				map.put(hdr, value);
			}
		}
		return new SimpleRow(map);
	}

}
