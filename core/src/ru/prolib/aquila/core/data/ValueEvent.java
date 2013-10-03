package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

/**
 * Событие связанное со значением.
 * <p>
 * @param <T> тип значения
 * <p>
 * 2012-04-28<br>
 * $Id: ValueEvent.java 565 2013-03-10 19:32:12Z whirlwind $
 */
public class ValueEvent<T> extends EventImpl {
	private final int index;
	private final T newValue, oldValue;

	/**
	 * Создать событие о добавлении нового значения.
	 * <p>
	 * @param type тип события
	 * @param newValue новое значение
	 * @param newValueIndex индекс нового значения
	 */
	public ValueEvent(EventType type, T newValue, int newValueIndex) {
		super(type);
		this.oldValue = null;
		this.newValue = newValue;
		this.index = newValueIndex;
	}
	
	/**
	 * Создать событие об обновлении существующего значения.
	 * <p>
	 * @param type тип события
	 * @param oldValue предыдущее значение
	 * @param newValue новое значение
	 * @param index индекс обновленного элемента
	 */
	public ValueEvent(EventType type, T oldValue, T newValue, int index) {
		super(type);
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.index = index;
	}
	
	/**
	 * Получить индекс добавленного значения.
	 * <p>
	 * @return индекс
	 */
	public int getValueIndex() {
		return index;
	}
	
	/**
	 * Получить новое изачение.
	 * <p> 
	 * @return значение
	 */
	public T getNewValue() {
		return newValue;
	}
	
	/**
	 * Получить предыдущее значение.
	 * <p>
	 * @return значение
	 */
	public T getOldValue() {
		return oldValue;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == ValueEvent.class ) {
			ValueEvent<?> o = (ValueEvent<?>) other;
			return new EqualsBuilder()
				.append(getType(), o.getType())
				.append(oldValue, o.oldValue)
				.append(newValue, o.newValue)
				.append(index, o.index)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()
			+ "[type=" + getType().toString() + ", "
			+ oldValue + "=>" + newValue + " at " + index + "]";
	}

}
