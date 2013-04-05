package ru.prolib.aquila.dde.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

/**
 * Базовое событие DDE-сервиса.
 * <p>
 * События данного типа характеризуются именем DDE-сервиса.
 * <p>
 * 2012-07-27<br>
 * $id$
 */
public class DDEEvent extends EventImpl {
	private final String service;

	/**
	 * Создать экземпляр события.
	 * <p>
	 * @param type тип события
	 * @param service имя DDE-сервиса
	 */
	public DDEEvent(EventType type, String service) {
		super(type);
		if ( service == null ) {
			throw new NullPointerException("Service cannot be null");
		}
		this.service = service;
	}
	
	/**
	 * Получить имя DDE-сервиса.
	 * <p>
	 * @return имя сервиса
	 */
	public String getService() {
		return service;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof DDEEvent ) {
			DDEEvent o = (DDEEvent)other;
			return new EqualsBuilder()
				.append(getType(), o.getType())
				.append(getService(), o.getService())
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121107, 92627)
			.append(getType())
			.append(service)
			.toHashCode();
	}

}
