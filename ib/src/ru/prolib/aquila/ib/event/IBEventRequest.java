package ru.prolib.aquila.ib.event;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventType;

/**
 * Событие: в связи с запросом (с номером запроса).
 * <p>
 * 2012-11-17<br>
 * $Id: IBEventRequest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventRequest extends IBEvent {
	private final int reqId;

	/**
	 * Создать событие.
	 * <p>
	 * @param type тип события
	 * @param reqId номер запроса
	 */
	public IBEventRequest(EventType type, int reqId) {
		super(type);
		this.reqId = reqId;
	}

	/**
	 * Получить номер запроса.
	 * <p>
	 * @return номер запроса
	 */
	public int getReqId() {
		return reqId;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == IBEventRequest.class ) {
			IBEventRequest o = (IBEventRequest) other;
			return new EqualsBuilder()
				.append(getType(), o.getType())
				.append(reqId, o.reqId)
				.isEquals();
		} else {
			return false;
		}
	}

}