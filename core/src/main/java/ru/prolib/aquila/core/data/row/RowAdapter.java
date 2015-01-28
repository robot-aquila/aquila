package ru.prolib.aquila.core.data.row;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Адаптер ряда.
 * <p>
 * Данный класс позволяет адаптировать любой источник данных к ряду. Конвертация
 * осуществляется по принципу делегирования запроса элемента ряда геттеру,
 * который соответствует запрошенному идентификатору. При этом, геттеру
 * в качестве аргумента передается объект-источник, указанный при создании
 * адаптера. При отсутствии соответствующего геттера возвращается null. 
 * <p>
 * 2013-02-15<br>
 * $Id$
 */
public class RowAdapter implements Row {
	private final Object source;
	private final Map<String, G<?>> adapters;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param source источник данных
	 * @param adapters набор адаптеров
	 */
	public RowAdapter(Object source, Map<String, G<?>> adapters) {
		super();
		this.source = source;
		this.adapters = adapters;
	}
	
	/**
	 * Получить источник данных.
	 * <p>
	 * @return источник данных
	 */
	public Object getSource() {
		return source;
	}
	
	/**
	 * Получить набор адаптеров.
	 * <p>
	 * @return набор адаптеров
	 */
	public Map<String, G<?>> getAdapters() {
		return adapters;
	}

	@Override
	public Object get(String name) throws RowException {
		try {
			G<?> adapter = adapters.get(name);
			return adapter == null ? null : adapter.get(source);
		} catch ( ValueException e ) {
			throw new RowException(e);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == RowAdapter.class ) {
			RowAdapter o = (RowAdapter) other;
			return new EqualsBuilder()
				.append(source, o.source)
				.append(adapters, o.adapters)
				.isEquals();
		} else {
			return false;
		}
	}

	@Override
	public Row getRowCopy() throws RowException {
		try {
			Map<String, Object> map = new Hashtable<String, Object>();
			for ( String header : adapters.keySet() ) {
				map.put(header, adapters.get(header).get(source));
			}
			return new SimpleRow(map);
		} catch ( ValueException e ) {
			throw new RowException(e);
		}
	}

}
