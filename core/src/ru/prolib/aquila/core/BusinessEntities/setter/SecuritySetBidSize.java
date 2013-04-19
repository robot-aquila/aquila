package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер размер спроса.
 * <p>
 * 2012-12-20<br>
 * $Id: SetSecurityBidSize.java 346 2012-12-20 16:48:36Z whirlwind $
 */
public class SecuritySetBidSize implements S<EditableSecurity> {
	
	/**
	 * Конструктор.
	 */
	public SecuritySetBidSize() {
		super();
	}

	@Override
	public void set(EditableSecurity object, Object value) throws ValueException {
		if ( value != null ) {
			Class<?> valueClass = value.getClass();
			if ( valueClass == Long.class ) {
				object.setBidSize((Long) value);
			} else if ( valueClass == Double.class ) {
				object.setBidSize(((Double) value).longValue());
			} else if ( valueClass == Integer.class ) {
				object.setBidSize(((Integer) value).longValue());
			}
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121221, 95849).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == SecuritySetBidSize.class;
	}

}
