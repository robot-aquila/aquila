package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;

/**
 * Событие в связи с запросом инструмента.
 */
public class RequestSecurityEvent extends EventImpl {
	private final SecurityDescriptor descr;
	private final int code;
	private final String msg;

	/**
	 * Конструктор.
	 * <p>
	 * @param type тип события
	 * @param descr дескриптор инструмента
	 * @param code целочисленный код события
	 * @param msg сопроводительное текстовое сообщение
	 */
	public RequestSecurityEvent(EventType type,
			SecurityDescriptor descr, int code, String msg)
	{
		super(type);
		this.descr = descr;
		this.code = code;
		this.msg = msg;
	}
	
	/**
	 * Получить дескриптор инструмента.
	 * <p>
	 * @return дескриптор инструмента, с которым связано событие
	 */
	public SecurityDescriptor getSecurityDescriptor() {
		return descr;
	}
	
	/**
	 * Получить целочисленный код события.
	 * <p>
	 * @return код события
	 */
	public int getCode() {
		return code;
	}
	
	/**
	 * Получить текстовое сообщение.
	 * <p>
	 * @return сопроводительное текстовое сообщение
	 */
	public String getMessage() {
		return msg;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != RequestSecurityEvent.class ) {
			return false;
		}
		RequestSecurityEvent o = (RequestSecurityEvent) other;
		return new EqualsBuilder()
			.appendSuper(o.isType(getType()))
			.append(o.descr, descr)
			.append(o.code, code)
			.append(o.msg, msg)
			.isEquals();
	}

}
