package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер текущей стоимости позиции.
 * <p>
 * 2012-12-30<br>
 * $Id: PositionSetMarketValue.java 390 2012-12-30 19:49:58Z whirlwind $
 */
public class PositionSetMarketValue implements S<EditablePosition> {
	
	/**
	 * Конструктор.
	 */
	public PositionSetMarketValue() {
		super();
	}

	@Override
	public void set(EditablePosition object, Object value) throws ValueException {
		if ( value != null ) {
			Class<?> valueClass = value.getClass();
			if ( valueClass == Double.class ) {
				object.setMarketValue((Double) value);
			}
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121231, 110351)
			.append(PositionSetMarketValue.class)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == PositionSetMarketValue.class;
	}

}
