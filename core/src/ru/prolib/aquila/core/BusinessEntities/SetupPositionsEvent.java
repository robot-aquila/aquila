package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

/**
 * Событие связанное со спецификацией позиций.
 * <p>
 * 2013-01-11<br>
 * $Id: SetupPositionsEvent.java 406 2013-01-11 10:08:56Z whirlwind $
 */
public class SetupPositionsEvent extends EventImpl {
	private final SetupPositions positions;

	/**
	 * Конструктор.
	 * <p>
	 * @param type тип события
	 * @param positions спецификация позиций
	 */
	public SetupPositionsEvent(EventType type, SetupPositions positions) {
		super(type);
		this.positions = positions;
	}

	/**
	 * Получить сетап позиций.
	 * <p>
	 * @return спецификация позиций
	 */
	public SetupPositions getPositions() {
		return positions;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == SetupPositionsEvent.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		SetupPositionsEvent o = (SetupPositionsEvent) other;
		return new EqualsBuilder()
			.append(getType(), o.getType())
			.append(positions, o.positions)
			.isEquals();
	}

}
