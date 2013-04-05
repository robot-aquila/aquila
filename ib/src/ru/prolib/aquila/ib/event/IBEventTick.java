package ru.prolib.aquila.ib.event;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.EventType;

/**
 * Событие в связи с тиковыми данными.
 * <p>
 * @link <a href="http://www.interactivebrokers.com/en/software/api/api.htm">TickTypes</a>
 * <p>
 * 2012-12-23<br>
 * $Id: IBEventTick.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventTick extends IBEventRequest {
	private final int tickType;
	private final double value;

	/**
	 * Конструктор.
	 * <p>
	 * @param type тип события
	 * @param reqId номер запроса
	 * @param tickType тип тиковых данных
	 * @param value
	 */
	public IBEventTick(EventType type, int reqId, int tickType, double value) {
		super(type, reqId);
		this.tickType = tickType;
		this.value = value;
	}
	
	/**
	 * Конструктор на основании существующего события.
	 * <p>
	 * @param type тип нового события
	 * @param event событие-основание
	 */
	public IBEventTick(EventType type, IBEventTick event) {
		this(type, event.getReqId(), event.getTickType(), event.getValue());
	}
	
	/**
	 * Получить тип тиковых данных.
	 * <p>
	 * @return тип тиковых данных
	 */
	public int getTickType() {
		return tickType;
	}
	
	/**
	 * Получить значение тика.
	 * <p>
	 * @return значение тика
	 */
	public double getValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == IBEventTick.class ) {
			IBEventTick o = (IBEventTick) other;
			return new EqualsBuilder()
				.append(getType(), o.getType())
				.append(getReqId(), o.getReqId())
				.append(tickType, o.tickType)
				.append(value, o.value)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121223, 41613)
			.append(getType())
			.append(getReqId())
			.append(tickType)
			.append(value)
			.toHashCode();
	}

}
