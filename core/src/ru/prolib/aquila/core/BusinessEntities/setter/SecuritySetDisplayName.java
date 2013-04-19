package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер наименования инструмента.
 * <p>
 * 2012-12-19<br>
 * $Id: SetSecurityDisplayName.java 344 2012-12-19 17:16:34Z whirlwind $
 */
public class SecuritySetDisplayName implements S<EditableSecurity> {
	
	/**
	 * Создать сеттер.
	 */
	public SecuritySetDisplayName() {
		super();
	}

	/**
	 * Установить наименование инструмента.
	 * <p>
	 * Допустимые значения типа {@link java.lang.String String}.
	 * Значения других типов игнорируются.
	 */
	@Override
	public void set(EditableSecurity object, Object value) throws ValueException {
		if ( value != null && value.getClass() == String.class ) {
			object.setDisplayName((String) value);
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121219, 203647).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass()==SecuritySetDisplayName.class;
	}

}
