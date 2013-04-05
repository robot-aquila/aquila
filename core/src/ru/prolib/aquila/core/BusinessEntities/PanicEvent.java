package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

/**
 * Событие, описывающее паническое состояние терминала.
 * <p>
 * 2013-02-04<br>
 * $Id$
 */
public class PanicEvent extends EventImpl {
	private final int code;
	private final String msgId;
	private final Object[] args;

	/**
	 * Конструктор.
	 * <p>
	 * @param type тип события
	 * @param code код состояния
	 * @param msgId идентификатор сообщения
	 * @param args аргументы события
	 */
	public PanicEvent(EventType type, int code, String msgId, Object[] args) {
		super(type);
		this.code = code;
		this.msgId = msgId;
		this.args = args;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param type тип события
	 * @param code код состояния
	 * @param msgId идентификатор сообщения
	 */
	public PanicEvent(EventType type, int code, String msgId) {
		this(type, code, msgId, new Object[] { });
	}

	/**
	 * Получить код состояния.
	 * <p>
	 * @return код состояния
	 */
	public int getCode() {
		return code;
	}
	
	/**
	 * Получить идентификатор сообщения.
	 * <p>
	 * @return идентификатор сообщения
	 */
	public String getMessageId() {
		return msgId;
	}
	
	/**
	 * Получить аргументы события.
	 * <p>
	 * @return аргументы события
	 */
	public Object[] getArgs() {
		return args;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == PanicEvent.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		PanicEvent o = (PanicEvent) other;
		return new EqualsBuilder()
			.append(getType(), o.getType())
			.append(code, o.code)
			.append(msgId, o.msgId)
			.append(args, o.args)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20130205, 191437)
			.append(getType())
			.append(code)
			.append(msgId)
			.append(args)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return getType().asString() + ".PanicEvent[" + code + "] " + msgId; 
	}

}
