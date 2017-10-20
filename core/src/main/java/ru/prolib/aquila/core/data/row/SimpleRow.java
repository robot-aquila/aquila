package ru.prolib.aquila.core.data.row;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Просто ряд объектов на основе массивов или хэш-массива.
 */
public class SimpleRow implements Row {
	private final Map<String, ?> map;
	
	public SimpleRow(Map<String, ?> map) {
		super();
		this.map = map;
	}
	
	public SimpleRow(String[] headers, Object[] values) {
		this(makeMap(headers, values));
	}
	
	/**
	 * Создать карту на основе двух массивов.
	 * <p>
	 * @param headers массив заголовков
	 * @param values массив соответствующих заголовкам значений
	 * @return карта
	 */
	public static Map<String, ?> makeMap(String[] headers, Object[] values) {
		Map<String, Object> map = new Hashtable<String, Object>();
		for ( int i = 0; i < headers.length; i ++ ) {
			map.put(headers[i], values[i]);
		}
		return map;
	}

	@Override
	public Object get(String name) {
		return map.get(name);
	}

	@Override
	public Row getRowCopy() {
		return new SimpleRow(new Hashtable<String, Object>(map));
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == SimpleRow.class ) {
			SimpleRow o = (SimpleRow) other;
			return new EqualsBuilder()
				.append(map, o.map)
				.isEquals();
		} else {
			return false;
		}
	}

}
