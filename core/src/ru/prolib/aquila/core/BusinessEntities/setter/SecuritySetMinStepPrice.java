package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.data.S;

/**
 * Сеттер стоимости минимального шага цены.
 * <p>
 * 2012-08-12<br>
 * $Id: SetSecurityMinStepPrice.java 252 2012-08-12 16:51:42Z whirlwind $
 */
public class SecuritySetMinStepPrice implements S<EditableSecurity> {
	
	/**
	 * Создать сеттер.
	 */
	public SecuritySetMinStepPrice() {
		super();
	}

	/**
	 * Установить стоимость минимального шага цены.
	 * <p>
	 * Допустимые типы значений {@link java.lang.Double} или null.
	 */
	@Override
	public void set(EditableSecurity security, Object value) {
		if ( value == null || value.getClass() == Double.class ) {
			security.setMinStepPrice((Double) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null
			&& other.getClass() == SecuritySetMinStepPrice.class;
	}

}
