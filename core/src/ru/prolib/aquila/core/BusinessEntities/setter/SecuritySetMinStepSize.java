package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.data.S;

/**
 * Сеттер размера минимального шага цены.
 * <p>
 * 2012-08-12<br>
 * $Id: SetSecurityMinStepSize.java 252 2012-08-12 16:51:42Z whirlwind $
 */
public class SecuritySetMinStepSize implements S<EditableSecurity> {
	
	/**
	 * Создать сеттер.
	 */
	public SecuritySetMinStepSize() {
		super();
	}

	/**
	 * Установить размер минимального шага цены.
	 * <p>
	 * Допустимый тип значений {@link java.lang.Double}. Значения других типов
	 * игнорируются.
	 */
	@Override
	public void set(EditableSecurity security, Object value) {
		if ( value != null && value.getClass() == Double.class ) {
			security.setMinStepSize((Double) value);
		}
	}

	@Override
	public boolean equals(Object other) {
		return other != null
			&& other.getClass() == SecuritySetMinStepSize.class;
	}
}
