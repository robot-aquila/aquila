package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер верхнего лимита цены.
 * <p>
 * 2012-08-12<br>
 * $Id: SetSecurityMaxPrice.java 252 2012-08-12 16:51:42Z whirlwind $
 */
public class SecuritySetMaxPrice implements S<EditableSecurity> {
	
	/**
	 * Создать сеттер.
	 */
	public SecuritySetMaxPrice() {
		super();
	}

	/**
	 * Установить верхний лимит цены.
	 * <p>
	 * Допустимые типы значений {@link java.lang.Double} или null.
	 */
	@Override
	public void set(EditableSecurity security, Object value) throws ValueException {
		if ( value == null || value.getClass() == Double.class ) {
			security.setMaxPrice((Double) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == SecuritySetMaxPrice.class;
	}

}
