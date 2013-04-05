package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditablePosition;

/**
 * Сеттер размера позиции на момент открытия.
 * <p>
 * 2012-09-16<br>
 * $Id$
 */
public class PositionSetOpenQty extends PositionSetLong {
	
	/**
	 * Создать сеттер.
	 */
	public PositionSetOpenQty() {
		super();
	}

	@Override
	protected void setProperty(EditablePosition position, Long value) {
		position.setOpenQty(value);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == PositionSetOpenQty.class ) {
			return true;
		} else {
			return false;
		}
	}

}
