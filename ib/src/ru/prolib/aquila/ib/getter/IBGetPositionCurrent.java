package ru.prolib.aquila.ib.getter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.ib.event.IBEventUpdatePortfolio;

/**
 * Геттер размера позиции на основании события {@link IBEventUpdatePortfolio}.
 * <p>
 * 2012-12-03<br>
 * $Id: IBGetPositionCurrent.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetPositionCurrent extends IBGetPositionAttr<Long> {
	
	/**
	 * Конструктор.
	 */
	public IBGetPositionCurrent() {
		super();
	}

	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == getClass() ) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121203, 15323)
			.append(IBGetPositionCurrent.class)
			.toHashCode();
	}

	@Override
	protected Long getEventAttr(IBEventUpdatePortfolio event) {
		return new Long(event.getPosition());
	}

}
