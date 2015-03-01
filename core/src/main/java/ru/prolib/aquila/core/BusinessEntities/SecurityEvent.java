package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.*;

/**
 * Событие связанное с инструментом торговли.
 * <p>
 * 2012-06-01<br>
 * $Id: SecurityEvent.java 250 2012-08-06 03:14:33Z whirlwind $
 */
public class SecurityEvent extends EventImpl {
	private final Security security;

	/**
	 * Создать событие.
	 * <p>
	 * @param type тип события
	 * @param security экземпляр инструмента
	 */
	public SecurityEvent(EventTypeSI type, Security security) {
		super(type);
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
				&& o.security == security;
		}
		return false;
	}

}
