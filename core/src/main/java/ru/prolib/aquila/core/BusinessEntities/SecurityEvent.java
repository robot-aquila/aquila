package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCEventImpl;

/**
 * Событие связанное с инструментом торговли.
 * <p>
 * 2012-06-01<br>
 * $Id: SecurityEvent.java 250 2012-08-06 03:14:33Z whirlwind $
 */
public class SecurityEvent extends OSCEventImpl {
	protected final Security security;

	/**
	 * Создать событие.
	 * <p>
	 * @param type тип события
	 * @param security экземпляр инструмента
	 * @param time - time of event
	 */
	public SecurityEvent(EventType type, Security security, Instant time) {
		super(type, security, time);
		this.security = security;
	}

	/**
	 * Получить экземпляр инструмента.
	 * <p>
	 * @return инструмент
	 */
	public Security getSecurity() {
		return security;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() == this.getClass() ) {
			SecurityEvent o = (SecurityEvent)other;
			return o.getType() == getType()
				&& o.security == security
				&& new EqualsBuilder()
					.append(o.time, time)
					.isEquals();
		}
		return false;
	}

}
