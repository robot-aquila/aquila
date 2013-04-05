package ru.prolib.aquila.ib.event;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventType;

/**
 * Событие: информация об ошибке.
 * <p>
 * 2012-11-17<br>
 * $Id: IBEventError.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventError extends IBEventRequest {
	private final int code;
	private final String msg;

	/**
	 * Создать событие.
	 * <p>
	 * @param type тип события
	 * @param reqId номер запроса
	 * @param code код ошибки
	 * @param msg текст ошибки
	 */
	public IBEventError(EventType type, int reqId, int code, String msg) {
		super(type, reqId);
		this.code = code;
		this.msg = msg;
	}
	
	/**
	 * Создать событие на основе другого экземпляра события.
	 * <p>
	 * @param type тип нового события
	 * @param event событие-основание
	 */
	public IBEventError(EventType type, IBEventError event) {
		this(type, event.getReqId(), event.getCode(), event.getMessage());
	}
	
	/**
	 * Получить код ошибки.
	 * <p>
	 * @return код ошибки
	 */
	public int getCode() {
		return code;
	}
	
	/**
	 * Получить текст ошибки.
	 * <p>
	 * @return текст ошибки
	 */
	public String getMessage() {
		return msg;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof IBEventError ) {
			IBEventError o = (IBEventError) other;
			return new EqualsBuilder()
				.appendSuper(super.equals(other))
				.append(code, o.code)
				.append(msg, o.msg)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "Req#" + getReqId() + " [" + code + "] " + msg;
	}

}
