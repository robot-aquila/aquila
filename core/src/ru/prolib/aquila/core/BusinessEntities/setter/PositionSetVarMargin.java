package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.data.S;

/**
 * Сеттер вариационной маржи позиции.
 * <p>
 * 2012-09-16<br>
 * $Id$
 */
public class PositionSetVarMargin implements S<EditablePosition> {
	
	/**
	 * Создать сеттер.
	 */
	public PositionSetVarMargin() {
		super();
	}

	/**
	 * Установить вариационную маржу позиции.
	 * <p>
	 * Допустимый тип значения {@link java.lang.Double}.
	 * Остальные типы значений игнорируются.
	 */
	@Override
	public void set(EditablePosition position, Object value) {
		if ( value != null ) {
			Class<?> valueClass = value.getClass(); 
			if ( valueClass == Double.class ) {
				position.setVarMargin((Double) value);
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null
		  && other.getClass() == PositionSetVarMargin.class )
		{
			return true;
		} else {
			return false;
		}
	}

}
