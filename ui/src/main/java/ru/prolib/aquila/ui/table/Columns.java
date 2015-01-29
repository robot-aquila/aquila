package ru.prolib.aquila.ui.table;

import java.util.*;


/**
 * Набор колонок.
 */
public class Columns {
	private final List<Column> columns;
	private final Map<String, Integer> id2index;
	
	public Columns() {
		super();
		columns = new Vector<Column>();
		id2index = new Hashtable<String, Integer>();
	}
	
	public synchronized Columns add(Column column) {
		int index = columns.size();
		columns.add(column);
		id2index.put(column.getId(), index);
		return this;
	}
	
	public synchronized int getCount() {
		return columns.size();
	}
	
	public synchronized Column get(int index) {
		return columns.get(index);
	}
	
	public synchronized Column get(String id) {
		return get(id2index.get(id));
	}

}