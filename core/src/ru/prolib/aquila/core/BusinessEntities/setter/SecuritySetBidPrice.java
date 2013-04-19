package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер цены спроса.
 * <p>
 * 2012-12-20<br>
 * $Id: SetSecurityBidPrice.java 346 2012-12-20 16:48:36Z whirlwind $
 */
public class SecuritySetBidPrice implements S<EditableSecurity> {
	
	/**
	 * Конструктор.
	 */
	public SecuritySetBidPrice() {
		super();
	}

	@Override
	public void set(EditableSecurity object, Object value) throws ValueException {
		if ( value != null ) {
			Class<?> valueClass = value.getClass();
			if ( valueClass == Long.class ) {
				object.setBidPrice(((Long) value).doubleValue());
			} else if ( valueClass == Double.class ) {
				object.setBidPrice((Double) value);
			} else if ( valueClass == Integer.class ) {
				object.setBidPrice(((Integer) value).doubleValue());
			}
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121221, 101323).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == SecuritySetBidPrice.class;
	}

}
