package ru.prolib.aquila.core.data.row;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Набор рядов на основе списка карт.
 * <p>
 * 2013-02-15<br>
 * $Id$
 */
public class ListOfMaps implements RowSet {
	private static int BEFORE_FIRST = -1;
	private static int AFTER_LAST = -2;
	private final List<Map<String, Object>> data;
	private int current = BEFORE_FIRST;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param data набор данных
	 */
	public ListOfMaps(List<Map<String, Object>> data) {
		super();
		this.data = data;
	}

	@Override
	public synchronized Object get(String name) {
		if ( current == AFTER_LAST || current == BEFORE_FIRST ) {
			return null;
		}
		return data.get(current).get(name);
	}

	@Override
	public synchronized boolean next() {
		if ( current == AFTER_LAST ) {
			return false;
		}
		current ++;
		if ( current >= data.size() ) {
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
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == ListOfMaps.class ) {
			ListOfMaps o = (ListOfMaps) other;
			return new EqualsBuilder()
				.append(current, o.current)
				.append(data, o.data)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public void close() {
		reset();
	}

}
