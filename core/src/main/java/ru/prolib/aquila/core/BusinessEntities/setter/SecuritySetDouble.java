package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер атрибута инструмента типа {@link java.lang.Double}.
 * <p>
 * 2012-12-29<br>
 * $Id: SetSecurityDouble.java 388 2012-12-30 12:58:15Z whirlwind $
 */
public abstract class SecuritySetDouble implements S<EditableSecurity> {
	
	/**
	 * Конструктор.
	 */
	public SecuritySetDouble() {
		super();
	}

	@Override
	public void set(EditableSecurity object, Object value) throws ValueException {
		if ( value != null ) {
			Class<?> valueClass = value.getClass();
			if ( valueClass == Long.class ) {
				setSecurityAttr(object, ((Long) value).doubleValue());
			} else if ( valueClass == Double.class ) {
				setSecurityAttr(object, (Double) value);
			} else if ( valueClass == Integer.class ) {
				setSecurityAttr(object, ((Integer) value).doubleValue());
			}
		}
	}
	
	/**
	 * Установить значение атрибута.
	 * <p>
	 * @param security инструмент
	 * @param value значение атрибута
	 */
	abstract protected
			void setSecurityAttr(EditableSecurity security, Double value);

}
