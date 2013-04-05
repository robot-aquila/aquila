package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.data.row.Row;

/**
 * Геттер именованного объекта ряда.
 * <p>
 * Значение элемента ряда направляется на вход геттеру-адаптеру, результат
 * работы которого возвращается. Если ряд не содержит элемента с указанным
 * именем или переданный объект не является экземпляром ряда, то возвращается
 * null.
 * <p>
 * 2012-10-13<br>
 * $Id: GRowObj.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class GRowObj<T> implements G<T> {
	private final G<T> adapter;
	private final String name;

	/**
	 * Создать геттер.
	 * <p>
	 * @param name идентификатор элемента ряда
	 */
	public GRowObj(String name) {
		this(name, null);
	}
	
	public GRowObj(String name, G<T> adapter) {
		super();
		this.name = name;
		this.adapter = adapter;
	}
	
	/**
	 * Получить идентификатор элемента ряда.
	 * <p>
	 * @return идентификатор
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Получить адаптер значения.
	 * <p>
	 * @return адаптер или null, если без адаптера
	 */
	public G<T> getAdapter() {
		return adapter;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(Object object) {
		if ( object instanceof Row ) {
			Object value = ((Row) object).get(name);
			return adapter == null ? (T) value : adapter.get(value);
		} else {
			return null;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other instanceof GRowObj ) {
			GRowObj<?> o = (GRowObj<?>) other;
			return new EqualsBuilder()
				.append(name, o.name)
				.append(adapter, o.adapter)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121031, /*0*/55359)
			.append(name)
			.append(adapter)
			.toHashCode();
	}

}
