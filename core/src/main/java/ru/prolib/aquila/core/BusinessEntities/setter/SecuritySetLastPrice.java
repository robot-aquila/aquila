package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер последней цены инструмента.
 * <p>
 * 2012-11-04<br>
 * $Id: SecuritySetLastPrice.java 302 2012-11-05 04:02:02Z whirlwind $
 */
public class SecuritySetLastPrice implements S<EditableSecurity> {
	
	/**
	 * Создать сеттер.
	 */
	public SecuritySetLastPrice() {
		super();
	}

	@Override
	public void set(EditableSecurity object, Object value) throws ValueException {
		if ( value == null || value.getClass() == Double.class ) {
			object.setLastPrice((Double) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof SecuritySetLastPrice;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121105, 140149).toHashCode();
	}

}
