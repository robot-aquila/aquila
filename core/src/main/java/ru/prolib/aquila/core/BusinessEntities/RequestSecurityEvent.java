package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;

/**
 * Событие в связи с запросом инструмента.
 */
public class RequestSecurityEvent extends EventImpl {
	private final Symbol symbol;
	private final int code;
	private final String msg;

	/**
	 * Конструктор.
	 * <p>
	 * @param type тип события
	 * @param symbol дескриптор инструмента
	 * @param code целочисленный код события
	 * @param msg сопроводительное текстовое сообщение
	 */
	public RequestSecurityEvent(EventType type,
			Symbol symbol, int code, String msg)
	{
		super(type);
		this.symbol = symbol;
		this.code = code;
		this.msg = msg;
	}
	
	/**
	 * Получить дескриптор инструмента.
	 * <p>
	 * @return дескриптор инструмента, с которым связано событие
	 */
	public Symbol getSymbol() {
		return symbol;
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
			.append(o.symbol, symbol)
			.append(o.code, code)
			.append(o.msg, msg)
			.isEquals();
	}

}
