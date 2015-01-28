package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;

/**
 * Сеттер минимальной цены инструмента за сессию.
 * <p>
 * 2012-12-29<br>
 * $Id: SetSecurityLowPrice.java 388 2012-12-30 12:58:15Z whirlwind $
 */
public class SecuritySetLowPrice extends SecuritySetDouble {
	
	/**
	 * Конструктор.
	 */
	public SecuritySetLowPrice() {
		super();
	}

	@Override
	protected void setSecurityAttr(EditableSecurity security, Double value) {
		security.setLowPrice(value);
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == SecuritySetLowPrice.class;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121229, 153905)
			.append(SecuritySetLowPrice.class)
			.toHashCode();
	}

}
