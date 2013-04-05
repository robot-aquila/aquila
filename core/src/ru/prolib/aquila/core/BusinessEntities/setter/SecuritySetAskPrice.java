package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.data.S;

/**
 * Сеттер цены предложения.
 * <p>
 * 2012-12-20<br>
 * $Id: SetSecurityAskPrice.java 346 2012-12-20 16:48:36Z whirlwind $
 */
public class SecuritySetAskPrice implements S<EditableSecurity> {
	
	/**
	 * Конструктор.
	 */
	public SecuritySetAskPrice() {
		super();
	}

	@Override
	public void set(EditableSecurity object, Object value) {
		if ( value != null ) {
			Class<?> valueClass = value.getClass();
			if ( valueClass == Long.class ) {
				object.setAskPrice(((Long) value).doubleValue());
			} else if ( valueClass == Double.class ) {
				object.setAskPrice((Double) value);
			} else if ( valueClass == Integer.class ) {
				object.setAskPrice(((Integer) value).doubleValue());
			}
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121221, 103157).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == SecuritySetAskPrice.class;
	}

}
