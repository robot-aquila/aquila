package ru.prolib.aquila.core.data;

import org.joda.time.DateTime;

/**
 * Заглушка темпоральной переменной.
 * <p> 
 * Данный класс обеспечивает возврат константного значения на любые запросы
 * независимо от датировки.  
 * <p>
 * @param <T> - тип значения
 */
public class StubTemporalValue<T> implements Aqtemporal<T> {
	private T value;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param value константное значение возврата
	 */
	public StubTemporalValue(T value) {
		super();
		this.value = value;
	}

	@Override
	public void close() {
		
	}

	@Override
	public T at(DateTime at) throws DataException {
		return value;
	}

}
