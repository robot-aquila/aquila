package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.data.S;

/**
 * Сеттер балансовой стоимости позиции.
 * <p>
 * 2012-12-30<br>
 * $Id: PositionSetBalanceCost.java 390 2012-12-30 19:49:58Z whirlwind $
 */
public class PositionSetBookValue implements S<EditablePosition> {
	
	/**
	 * Конструктор.
	 */
	public PositionSetBookValue() {
		super();
	}

	@Override
	public void set(EditablePosition object, Object value) {
		if ( value != null ) {
			Class<?> valueClass = value.getClass();
			if ( valueClass == Double.class ) {
				object.setBookValue((Double) value);
			}
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121231, 123911)
			.append(PositionSetBookValue.class)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == PositionSetBookValue.class;
	}

}
