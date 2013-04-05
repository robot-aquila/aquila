package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditablePosition;

/**
 * Сеттер текущего размера позиции.
 * <p>
 * 2012-09-16<br>
 * $Id$
 */
public class PositionSetCurrQty extends PositionSetLong {
	
	/**
	 * Создать сеттер.
	 */
	public PositionSetCurrQty() {
		super();
	}

	@Override
	protected void setProperty(EditablePosition position, Long value) {
		position.setCurrQty(value);
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == PositionSetCurrQty.class;
	}

}
