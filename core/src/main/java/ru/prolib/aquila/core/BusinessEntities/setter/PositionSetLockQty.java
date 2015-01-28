package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditablePosition;

/**
 * Сеттер заблокированного размера позиции.
 * <p>
 * 2012-09-16<br>
 * $Id$
 */
public class PositionSetLockQty extends PositionSetLong {
	
	/**
	 * Создать сеттер.
	 */
	public PositionSetLockQty() {
		super();
	}

	@Override
	protected void setProperty(EditablePosition position, Long value) {
		position.setLockQty(value);
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == PositionSetLockQty.class;
	}

}
