package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

/**
 * Событие связанное с торговой позицией.
 * <p>
 * 2012-08-03<br>
 * $Id: PositionEvent.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public class PositionEvent extends EventImpl {
	private final Position position;

	/**
	 * Создать событие.
	 * <p>
	 * @param type тип события
	 * @param position объект позиции
	 */
	public PositionEvent(EventType type, Position position) {
		super(type);
		this.position = position;
	}
	
	/**
	 * Получить объект позиции.
	 * <p>
	 * @return позиция
	 */
	public Position getPosition() {
		return position;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() == this.getClass() ) {
			PositionEvent o = (PositionEvent)other;
			return o.getType() == getType()
				&& o.getPosition() == getPosition();
		}
		return false;
	}
	
	@Override
	public String toString() {
		return getType().toString() + " ["
			+ "acc=" + position.getAccount() + ", "
			+ "sec=" + position.getSecurityDescriptor() + ", "
			+ "open=" + position.getOpenQty() + ", "
			+ "curr=" + position.getCurrQty() + ", "
			+ "lock=" + position.getLockQty() + ", "
			+ "vmargin=" + position.getVarMargin() + ", "
			+ "book.val=" + position.getBookValue() + ", "
			+ "mkt.val=" + position.getMarketValue() + "]";
	}

}
