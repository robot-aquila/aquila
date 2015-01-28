package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Базовый сеттер long-атрибута позиции.
 * <p>
 * 2012-09-16<br>
 * $Id$
 */
public abstract class PositionSetLong implements S<EditablePosition> {

	/**
	 * Создать сеттер.
	 */
	public PositionSetLong() {
		super();
	}

	/**
	 * Установить значение атрибута позиции.
	 * <p>
	 * Допустимые типы значений: {@link java.lang.Long},
	 * {@link java.lang.Integer} или {@link java.lang.Double}. В случае
	 * вещественного выполняется приведение к типу long с потерей точности.
	 */
	@Override
	public void set(EditablePosition position, Object value) throws ValueException {
		if ( value != null ) {
			Class<?> valueClass = value.getClass();
			if ( valueClass == Long.class ) {
				setProperty(position, (Long) value);
			} else if ( valueClass == Integer.class ) {
				setProperty(position, ((Integer) value).longValue());
			} else if ( valueClass == Double.class ) {
				setProperty(position, ((Double) value).longValue());
			}
		}
	}
	
	abstract protected void setProperty(EditablePosition position, Long value);

}