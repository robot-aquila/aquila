package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

import ru.prolib.aquila.core.*;

/**
 * Security tick event.
 * <p>
 * 2012-06-01<br>
 * $Id: SecurityTradeEvent.java 223 2012-07-04 12:26:58Z whirlwind $
 */
public class SecurityTickEvent extends SecurityEvent {
	private final Tick tick;

	/**
	 * Constructor.
	 * <p>
	 * @param type - event type
	 * @param security - the security instance
	 * @param time - time of event
	 * @param tick - data tick
	 */
	public SecurityTickEvent(EventType type, Security security, Instant time, Tick tick) {
		super(type, security, time);
		this.tick = tick;
	}

	/**
	 * Получить тик по инструменту
	 * <p>
	 * @return тик
	 */
	public Tick getTick() {
		return tick;
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
			SecurityTickEvent o = (SecurityTickEvent)other;
			return o.getType() == getType()
				&& o.getSecurity() == getSecurity()
				&& tick.equals(o.tick);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return getType().toString() + "[" + tick + "]";
	}

}
