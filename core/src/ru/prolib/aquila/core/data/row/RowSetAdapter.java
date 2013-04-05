package ru.prolib.aquila.core.data.row;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.data.G;

/**
 * Адаптер одного набора рядов к другому набору рядов.
 * <p>
 * Работает по принципу {@link RowAdapter}, но требует в качестве источника
 * объекта типа {@link RowSet}. Делегирует методы навигации по набору рядов 
 * соответствующим методам исходного набора. Предназначен для конвертации
 * рядов одного набора в ряды другого набора. Так же выполняет кеширование
 * значений ряда и сохраняет их до перехода к другому ряду. 
 * <p>
 * 2013-02-15<br>
 * $Id$
 */
public class RowSetAdapter extends RowAdapter implements RowSet {
	private final RowSet rows;
	private final Map<String, Object> cache;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param source исходный набор рядов
	 * @param adapters набор адаптеров
	 */
	public RowSetAdapter(RowSet source, Map<String, G<?>> adapters) {
		super(source, adapters);
		rows = source;
		cache = new HashMap<String, Object>();
	}

	@Override
	public synchronized Object get(String name) {
		Object value = cache.get(name);
		if ( value == null ) {
			value = super.get(name);
			if ( value != null ) {
				cache.put(name, value);
			}
		}
		return value;
	}

	@Override
	public synchronized boolean next() {
		resetCache();
		return rows.next();
	}

	@Override
	public synchronized void reset() {
		resetCache();
		rows.reset();
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == RowSetAdapter.class ) {
			RowSetAdapter o = (RowSetAdapter) other;
			return new EqualsBuilder()
				.append(rows, o.rows)
				.append(getAdapters(), o.getAdapters())
				.isEquals();
		} else {
			return false;
		}
	}
	
	/**
	 * Сбросить кэш.
	 */
	private synchronized void resetCache() {
		cache.clear();
	}

	@Override
	public synchronized void close() {
		resetCache();
		rows.close();
	}

}
