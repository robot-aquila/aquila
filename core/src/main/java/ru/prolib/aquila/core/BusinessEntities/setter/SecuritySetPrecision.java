package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер точности цены торгового инструмента.
 * <p>
 * 2012-08-12<br>
 * $Id: SetSecurityPrecision.java 252 2012-08-12 16:51:42Z whirlwind $
 */
public class SecuritySetPrecision implements S<EditableSecurity> {
	
	/**
	 * Создать сеттер.
	 */
	public SecuritySetPrecision() {
		super();
	}

	/**
	 * Установить точность цены инструмента.
	 * <p>
	 * Допустимые типы значений {@link java.lang.Double} или
	 * {@link java.lang.Integer}. Значения иных типов игнорируются.
	 */
	@Override
	public void set(EditableSecurity security, Object value) throws ValueException {
		if ( value != null ) {
			Class<?> valueClass = value.getClass();
			if ( valueClass == Integer.class ) {
				security.setPrecision((Integer) value);
			} else if ( valueClass == Double.class ) {
				security.setPrecision(((Double) value).intValue());
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof SecuritySetPrecision;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121105, 155535).toHashCode();
	}

}
