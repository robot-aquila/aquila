package ru.prolib.aquila.ib.event;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.EventType;

/**
 * Базовое событие в связи с заявкой.
 * <p>
 * 2012-12-11<br>
 * $Id: IBEventOrder.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventOrder extends IBEvent {
	private final int orderId;

	/**
	 * Конструктор.
	 * <p>
	 * @param type тип события
	 * @param orderId номер заявки
	 */
	public IBEventOrder(EventType type, int orderId) {
		super(type);
		this.orderId = orderId;
	}
	
	/**
	 * Получить номер заявки.
	 * <p>
	 * @return номер заявки
	 */
	public int getOrderId() {
		return orderId;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBEventOrder.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		IBEventOrder o = (IBEventOrder) other;
		return new EqualsBuilder()
			.append(getType(), o.getType())
			.append(orderId, o.orderId)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121211, 102449)
			.append(getType())
			.append(orderId)
			.toHashCode();
	}

}
