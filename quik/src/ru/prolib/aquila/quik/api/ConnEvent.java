package ru.prolib.aquila.quik.api;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.t2q.T2QConnStatus;

/**
 * Событие смены статуса подключения QUIK.
 */
public class ConnEvent extends EventImpl {
	private final T2QConnStatus status;

	public ConnEvent(EventType type, T2QConnStatus status) {
		super(type);
		this.status = status;
	}
	
	/**
	 * Получить статус подключения QUIK.
	 * <p>
	 * @return статус подключения
	 */
	public T2QConnStatus getStatus() {
		return status;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == ConnEvent.class ) {
			ConnEvent o = (ConnEvent) other;
			return new EqualsBuilder()
				.append(getType(), o.getType())
				.append(status, o.status)
				.isEquals();
		} else {
			return false;
		}
	}

}
