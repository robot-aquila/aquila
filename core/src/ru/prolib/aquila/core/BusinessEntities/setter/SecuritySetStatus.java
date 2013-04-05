package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.SecurityStatus;
import ru.prolib.aquila.core.data.S;

/**
 * Сеттер статуса инструмента.
 * <p>
 * 2012-12-29<br>
 * $Id: SetSecurityStatus.java 388 2012-12-30 12:58:15Z whirlwind $
 */
public class SecuritySetStatus implements S<EditableSecurity> {
	
	/**
	 * Создать конструктор.
	 */
	public SecuritySetStatus() {
		super();
	}

	@Override
	public void set(EditableSecurity object, Object value) {
		if ( value instanceof SecurityStatus ) {
			object.setStatus((SecurityStatus) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == SecuritySetStatus.class;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121229, 161851)
			.append(SecuritySetStatus.class)
			.toHashCode();
	}

}
