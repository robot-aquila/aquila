package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;

/**
 * Сеттер цены открытия по инструменту.
 * <p>
 * 2012-12-29<br>
 * $Id: SetSecurityOpenPrice.java 388 2012-12-30 12:58:15Z whirlwind $
 */
public class SecuritySetOpenPrice extends SecuritySetDouble {
	
	/**
	 * Конструктор.
	 */
	public SecuritySetOpenPrice() {
		super();
	}

	@Override
	protected void setSecurityAttr(EditableSecurity security, Double value) {
		security.setOpenPrice(value);
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == SecuritySetOpenPrice.class;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121229, 112819)
			.append(SecuritySetOpenPrice.class)
			.toHashCode();
	}

}
