package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер размера лота торгового инструмента.
 * <p>
 * 2012-08-12<br>
 * $Id: SetSecurityLotSize.java 252 2012-08-12 16:51:42Z whirlwind $
 */
public class SecuritySetLotSize implements S<EditableSecurity> {
	
	/**
	 * Создать сеттер.
	 */
	public SecuritySetLotSize() {
		super();
	}

	/**
	 * Установить размер лота инструмента.
	 * <p>
	 * Допустимые типы значений {@link java.lang.Integer} или
	 * {@link java.lang.Double}. В случае вещественного используется приведение
	 * к типу int. Остальные типы значений игнорируются.
	 */
	@Override
	public void set(EditableSecurity security, Object value) throws ValueException {
		if ( value != null ) {
			Class<?> valueClass = value.getClass(); 
			if ( valueClass == Double.class ) {
				security.setLotSize(((Double)value).intValue());
			} else if ( valueClass == Integer.class ) {
				security.setLotSize((Integer)value);
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == SecuritySetLotSize.class;
	}

}
